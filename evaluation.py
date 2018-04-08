def read_matrix(file):
    """Read file as matrix."""
    f = open(file, 'r')
    matrix = [[float(x) for x in line.strip().split(' ')] for line in f]
    f.close()
    return matrix


def extract_vector(matrix, references):
    """
    Creates a distance vector of non-reference signatures to reference ones.
    The values are normalized by the average distance.

    :param matrix: distance matrix
    :param references: list of reference indices
    :return: distance vector

    """
    w = len(matrix[0])
    n = len(references)

    # deepcopy
    matrix = [row.copy() for row in matrix]

    # Remove non-reference rows
    for row in reversed(range(0, n)):
        if row not in references:
            matrix.pop(row)

    # move reference columns to front
    i = 0
    for col in references:
        for row in matrix:
            row.insert(i, row.pop(col))
        i += 1

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
        [distance, genuine, true_positive, false_positive, true_pos_rate, false_pos_rate, false_neg_rate]

    :param vector: distance vector
    :param ground_truth: list of 0 and 1 indicating genuine signatures
    :return: information table
    """
    tuples = zip(vector, ground_truth)
    table = list(map(list, tuples))
    table.sort()

    n_genuine = sum(ground_truth)
    n_forgery = len(ground_truth) - n_genuine
    true_pos = false_pos = 0
    for row in table:
        is_genuine = row[1]
        if is_genuine:
            true_pos += 1
        else:
            false_pos += 1

        row.append(true_pos)
        row.append(false_pos)
        row.append(true_pos / n_genuine)
        row.append(false_pos / n_forgery)
        row.append(1 - true_pos / n_genuine)

    return table


def get_eer(table):
    """Calculates the equal error rate (EER)."""
    n = len(table)
    n_genuine = sum(row[1] for row in table)

    for row in table:
        false_pos_rate = row[5]
        false_neg_rate = row[6]
        if false_neg_rate <= false_pos_rate:
            true_pos = row[2]
            false_pos = row[3]
            eer = (n_genuine - true_pos + false_pos) / n
            return eer
