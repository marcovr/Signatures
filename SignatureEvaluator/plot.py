import matplotlib.pyplot as plt


def plot_dist(vectors, ground_truth, file=''):
    """
    Plot distance vector distribution graph.

    :param vectors: list of distance vectors to compare
    :param ground_truth: list of 0 and 1 indicating genuine signatures
    :param file: plot output file (graph is shown if blank)
    """
    n = len(vectors)
    g_x = []
    f_x = []
    g_y = []
    f_y = []

    # split data in genuine / forgery & create y values
    for k, vector in enumerate(vectors):
        user = k + 1
        truth = pad_flags(ground_truth, len(vector))
        for genuine, value in zip(truth, vector):
            if genuine:
                g_x.append(value)
                g_y.append(user)
            else:
                f_x.append(value)
                f_y.append(user)

    # set plot height proportional to number of vectors
    plt.figure(figsize=(5, 1 + 0.3 * n))

    # create plot
    plt.plot(f_x, f_y, 'x', markersize=10)
    plt.plot(g_x, g_y, 'o', markersize=10, markerfacecolor='None')

    # decorate plot
    plt.yticks(range(1, n + 1))
    plt.ylim(0, n + 1)
    plt.gca().invert_yaxis()
    plt.title('distance distribution')
    plt.xlabel('graph edit distance')
    plt.ylabel('signature sets')
    plt.tight_layout()
    show_or_save(file)


def plot_det(table, log=False, file=''):
    """
    Plot DET curve.

    :param table: evaluation table
    :param log: use logarithmic scale
    :param file: plot output file (graph is shown if blank)
    """
    fpr = [row["false_pos_rate"] for row in table]
    fnr = [row["false_neg_rate"] for row in table]

    # create plot
    if log:
        plt.loglog(fpr, fnr)
    else:
        plt.plot(fpr, fnr)

    # decorate plot
    plt.grid()
    plt.title('DET curve')
    plt.xlabel('false positive rate')
    plt.ylabel('false negative rate')
    plt.tight_layout()
    show_or_save(file)


def show_or_save(file):
    if file == '':
        plt.show()
    else:
        plt.draw()
        plt.savefig(file)


def pad_flags(flags, l):
    return flags + [0] * (l - len(flags))
