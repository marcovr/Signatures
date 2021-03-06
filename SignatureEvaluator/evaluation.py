class EvaluationData:
    def __init__(self, file, references, ground_truth):
        """
        Creates a new EvaluationData object, which provides different useful methods.

        :param file: path to GED matrix file
        :param references: list of reference indices
        :param ground_truth: list of 0 and 1 indicating genuine signatures
        """
        self.file = file
        self.references = references
        self.ground_truth = ground_truth
        self._matrix = read_matrix(file)
        self._vector = None
        self._vector_raw = None
        self._table = None
        self._eer = None

        # preprocess data for further use
        pad_flags(ground_truth, len(self._matrix[0]) - len(references))
        strip_non_ref_rows(self._matrix, references)
        move_ref_cols_front(self._matrix, references)
        self.norm = get_norm_factor(self._matrix, references)

    def matrix(self):
        """returns the preprocessed GED matrix"""
        return self._matrix

    def vector(self, normalize=True):
        """returns the (normalized) distance vector of non-reference signatures to reference ones."""
        if normalize:
            if self._vector is None:
                self._vector = extract_vector(self.matrix(), self.references, self.norm)
            return self._vector
        else:
            if self._vector_raw is None:
                self._vector_raw = extract_vector(self.matrix(), self.references)
            return self._vector_raw

    def table(self):
        """
        Creates a table with each row containing:
            distance, genuine, true_positive, false_positive, true_pos_rate, false_pos_rate, false_neg_rate
        """
        if self._table is None:
            self._table = create_table(self.vector(), self.ground_truth)
        return self._table

    def eer(self):
        """Calculates the equal error rate (EER)."""
        if self._eer is None:
            self._eer = get_eer(self.table())
        return self._eer

    def verify(self, file):
        """
        Verify signatures by comparing GED values against the threshold determined with the EER.
        Requires the same reference signatures being used.

        :param file: GED matrix file with signatures to verify
        :return: list of True / False indicating accepted / rejected signatures
        """
        u_matrix = read_matrix(file)
        strip_non_ref_rows(u_matrix, self.references)
        unverified = extract_vector(u_matrix, [], self.norm)

        threshold = get_threshold(self.table())
        return [x <= threshold for x in unverified]


class EvaluationGroup:
    def __init__(self, data):
        """
        Creates a new EvaluationGroup object, in order to apply methods to a combination of objects.

        :param data: list of EvaluationData objects
        """
        self.data = data
        self.ground_truth = []
        for d in data:
            self.ground_truth.extend(d.ground_truth)
        self._vector = None
        self._vector_raw = None
        self._table = None
        self._eer = None

    def vector(self, normalize=True):
        """returns the (normalized) distance vector of the whole group as one."""
        if normalize:
            if self._vector is None:
                self._vector = []
                for d in self.data:
                    self._vector.extend(d.vector())
            return self._vector
        else:
            if self._vector_raw is None:
                self._vector_raw = []
                for d in self.data:
                    self._vector_raw.extend(d.vector(False))
            return self._vector_raw

    def table(self):
        """Creates the evaluation table for the whole group as one."""
        if self._table is None:
            self._table = create_table(self.vector(), self.ground_truth)
        return self._table

    def eer(self):
        """Calculates the equal error rate for the whole group as one."""
        if self._eer is None:
            self._eer = get_eer(self.table())
        return self._eer


def read_matrix(file):
    """Read file as matrix."""
    with open(file, 'r') as f:
        matrix = [[float(x) for x in line.strip().split(' ')] for line in f]
    return matrix


def strip_non_ref_rows(matrix, references):
    """Removes non-reference rows."""
    h = len(matrix)

    for row in reversed(range(0, h)):
        if row not in references:
            matrix.pop(row)


def move_ref_cols_front(matrix, references):
    """Reorders columns such that reference columns are first."""
    for i, col in enumerate(references):
        for row in matrix:
            row.insert(i, row.pop(col))


def get_norm_factor(matrix, references):
    """Calculates user normalization factor (average of reference distances)."""
    n = len(references)

    min_sum = 0
    for x in range(0, n):
        minimum = 1
        for y in range(0, n):
            # skip distances to self
            if x != y:
                minimum = min(minimum, matrix[y][x])
        min_sum += minimum
    return min_sum / n


def extract_vector(matrix, references, norm=1):
    """
    Creates a distance vector of non-reference signatures to reference ones.

    :param matrix: distance matrix
    :param references: list of reference indices
    :param norm: user normalization value (default 1)
    :return: distance vector
    """
    w = len(matrix[0])
    h = len(matrix)
    n = len(references)

    # create normalized distance vector
    vector = []
    for x in range(n, w):
        minimum = matrix[0][x] / norm
        for y in range(1, h):
            minimum = min(minimum, matrix[y][x] / norm)
        vector.append(minimum)

    return vector


def create_table(vector, ground_truth):
    """
    Creates a table with each row containing:
        distance, genuine, true_positive, false_positive, true_pos_rate, false_pos_rate, false_neg_rate

    :param vector: distance vector
    :param ground_truth: list of 0 and 1 indicating genuine signatures
    :return: evaluation table
    """
    tuples = list(zip(vector, ground_truth))
    tuples.sort()

    table = []
    n_genuine = sum(ground_truth)
    n_forgery = len(ground_truth) - n_genuine
    true_pos = false_pos = 0
    for (distance, is_genuine) in tuples:
        if is_genuine:
            true_pos += 1
        else:
            false_pos += 1

        row = {
            "distance": distance,
            "genuine": is_genuine,
            "true_positive": true_pos,
            "false_positive": false_pos,
            "true_pos_rate": true_pos / n_genuine,
            "false_pos_rate": false_pos / n_forgery,
            "false_neg_rate": 1 - true_pos / n_genuine
        }
        table.append(row)

    return table


def get_eer(table):
    """Calculates the equal error rate (EER)."""
    n = len(table)
    n_genuine = sum(row["genuine"] for row in table)

    for row in table:
        if row["false_neg_rate"] <= row["false_pos_rate"]:
            tp = row["true_positive"]
            fp = row["false_positive"]
            eer = (n_genuine - tp + fp) / n
            return eer
    return 1


def get_threshold(table):
    """Calculates the threshold value for genuine signatures."""
    n = len(table)
    for i, row in enumerate(table):
        if row["false_neg_rate"] < row["false_pos_rate"]:
            if i == 0:
                return row["distance"]
            else:
                return (row["distance"] + table[i - 1]["distance"]) / 2
        elif row["false_neg_rate"] == row["false_pos_rate"]:
            if i == n:
                return row["distance"]
            else:
                return (row["distance"] + table[i + 1]["distance"]) / 2
    return 0


def pad_flags(flags, l):
    flags.extend([0] * (l - len(flags)))
