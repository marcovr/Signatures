import os
import sys
import getopt
import evaluation
import plot


# example: python run.py -r 10 -g 5 data


def usage():
    print("usage: %s (-g INT | -G FLAGS) (-r INT | -R FLAGS) [OPTIONS] INPUT..." % sys.argv[0])
    print()
    print("-g INT      number indicating amount of genuine signatures (0 to N)")
    print("-G FLAGS    flags specifying genuine signatures (1 = genuine, 0 = forgery)")
    print("   Notice:  only non-reference signatures are considered (as list 0 to K).")
    print()
    print("-r INT      number indicating amount of reference signatures (0 to N)")
    print("-R FLAGS    flags specifying reference signatures (1 = reference, 0 = non r.)")
    print()
    print("OPTIONS:")
    print("-p FILE     plot DET curve")
    print("-P FILE     plot distance vector distribution graph")
    print("   Notice:  graphs are written to FILE or shown if FILE is ''")
    print()
    print("-t          print evaluation table")
    print()
    print("INPUT       directory containing input files or list of input files")


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'g:G:r:R:p:P:t')
    except getopt.GetoptError:
        usage()
        sys.exit(2)

    reference = ground_truth = None
    show_table = False
    plot_dist = plot_det = None

    for opt, arg in opts:
        if opt == '-t':
            show_table = True
        elif opt == '-g':
            ground_truth = [1] * int(arg)
        elif opt == '-G':
            ground_truth = map(int, arg)
        elif opt == '-r':
            reference = range(0, int(arg))
        elif opt == '-R':
            reference = to_indices(map(int, arg))
        elif opt == '-p':
            plot_det = arg
        elif opt == '-P':
            plot_dist = arg
        else:
            usage()
            sys.exit(2)

    if len(args) == 0 or reference is None or ground_truth is None:
        usage()
        sys.exit(2)

    files = args
    if len(files) == 1:
        file = files[0]
        if os.path.isdir(file):
            files = expand_dir(file)

    print("processing %s file(s)" % len(files))

    vectors = []
    ground_truths = []
    matrices = [evaluation.read_matrix(file) for file in files]
    for matrix in matrices:
        vector = evaluation.extract_vector(matrix, reference)
        vectors.extend(vector)
        truth = pad_flags(ground_truth, len(vector))
        ground_truths.extend(truth)

    table = evaluation.create_table(vectors, ground_truths)
    print(evaluation.get_eer(table))

    if show_table:
        print()
        print("\t".join(["distance", "genuine", "true_positive", "false_positive",
                         "true_pos_rate", "false_pos_rate", "false_neg_rate"]))
        for row in table:
            print("\t".join(map(str, row)))

    if plot_det is not None:
        plot.plot_det(table, False, plot_det)
    if plot_dist is not None:
        vectors = [evaluation.extract_raw_vector(matrix, reference) for matrix in matrices]
        plot.plot_dist(vectors, ground_truth, plot_dist)


def to_indices(flags):
    indices = []
    for i, v in enumerate(flags):
        if v:
            indices.append(i)
    return indices


def pad_flags(flags, l):
    return flags + [0] * (l - len(flags))


def expand_dir(dir):
    files = os.listdir(dir)
    return [os.path.join(dir, f) for f in files]


main()
