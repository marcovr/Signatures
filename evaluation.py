class EvaluationData:
    def __init__(self, file, references, ground_truth, unverified=None):
        self.file = file
        self.references = references
        self.ground_truth = ground_truth
        self._unverified = unverified
        self._matrix = read_matrix(file)
        self._vector = None
        self._vector_raw = None
        self._table = None
        self._eer = None

        # extract unverified columns
        if unverified is not None:
            u = []
            vector = self.vector()
            for i in reversed(unverified):
                u.insert(0, vector.pop(i))
                for row in self._matrix:
                    row.pop(i)
            self._unverified = u

        pad_flags(ground_truth, len(self._matrix[0]) - len(references))

    def matrix(self):
        return self._matrix

    def vector(self, normalize=True):
        if normalize:
            if self._vector is None:
                self._vector = extract_vector(self.matrix(), self.references)
            return self._vector
        else:
            if self._vector_raw is None:
                self._vector_raw = extract_vector(self.matrix(), self.references, False)
            return self._vector_raw

    def table(self):
        if self._table is None:
            self._table = create_table(self.vector(), self.ground_truth)
        return self._table

    def eer(self):
        if self._eer is None:
            self._eer = get_eer(self.table())
        return self._eer

    def verify(self):
        threshold = get_threshold(self.table())
        return [x < threshold for x in self._unverified]


class EvaluationGroup:
    def __init__(self, data):
        self.data = data
        self.ground_truth = []
        for d in data:
            self.ground_truth.extend(d.ground_truth)
        self._vector = None
        self._vector_raw = None
        self._table = None
        self._eer = None

    def vector(self, normalize=True):
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
        if self._table is None:
            self._table = create_table(self.vector(), self.ground_truth)
        return self._table

    def eer(self):
        if self._eer is None:
            self._eer = get_eer(self.table())
        return self._eer


def read_matrix(file):
    """Read file as matrix."""
    f = open(file, 'r')
    matrix = [[float(x) for x in line.strip().split(' ')] for line in f]
    f.close()
    return matrix


def extract_vector(matrix, references, normalize=True):
    """
    Creates a distance vector of non-reference signatures to reference ones.

    :param matrix: distance matrix
    :param references: list of reference indices
    :param normalize: normalize values by the average distance (default true)
    :return: distance vector

    """
    w = len(matrix[0])
    h = len(matrix)
    n = len(references)

    # deepcopy
    matrix = [row.copy() for row in matrix]

    # Remove non-reference rows
    for row in reversed(range(0, h)):
        if row not in references:
            matrix.pop(row)

    # move reference columns to front
    for i, col in enumerate(references):
        for row in matrix:
            row.insert(i, row.pop(col))

    if normalize:
        # calculate user normalization factor
        min_sum = 0
        for x in range(0, n):
            minimum = 1
            for y in range(0, n):
                # skip distances to self
                if x != y:
                    minimum = min(minimum, matrix[y][x])
            min_sum += minimum
        norm = min_sum / n
    else:
        norm = 1

    # create normalized distance vector
    vector = []
    for x in range(n, w):
        minimum = matrix[0][x] / norm
        for y in range(1, n):
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
    for i, row in enumerate(table):
        if row["false_neg_rate"] <= row["false_pos_rate"]:
            if i == 0:
                return row["distance"]
            else:
                return (row["distance"] + table[i - 1]["distance"]) / 2
    return 0


def pad_flags(flags, l):
    flags.extend([0] * (l - len(flags)))
