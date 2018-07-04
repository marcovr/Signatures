import os
import sys
import json
import getopt
from evaluation import EvaluationData, EvaluationGroup


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
    print("-l          use logarithmic scale (DET curve only)")
    print()
    print("-v FILE     verify signatures from GED file.")
    print("   Notice:  requires the same reference signatures being used.")
    print()
    print("-j FILE     output results to json file.")
    print("-t          output evaluation table")
    print("-s          silent mode. Raw results only.")
    print()
    print("INPUT       directory containing input files (GED) or list of input files")


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'g:G:r:R:p:P:tlv:sj:')
    except getopt.GetoptError:
        usage()
        sys.exit(2)

    reference = ground_truth = None
    show_table = False
    plot_dist = plot_det = None
    log = False
    normalize_graph = False
    verify_file = json_file = None
    silent = False

    for opt, arg in opts:
        if opt == '-t':
            show_table = True
        elif opt == '-l':
                log = True
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
        elif opt == '-v':
            verify_file = arg
        elif opt == '-j':
            json_file = arg
        elif opt == '-s':
            silent = True
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

    if not silent:
        print("processing %s file(s)" % len(files))

    data = [EvaluationData(file, reference, ground_truth) for file in files]
    group = EvaluationGroup(data)
    results = {}

    if not silent:
        print("EER: %s" % group.eer())
    results["eer"] = group.eer()

    if verify_file is not None:
        if len(group.data) == 1:
            results["verified"] = group.data[0].verify(verify_file)
        else:
            results["verified"] = "Cannot verify signatures against multiple users."
        if not silent:
            print("Signature accepted: %s" % results["verified"])

    if show_table:
        results["table"] = group.table()
        print()
        if not silent:
            print("Evaluation table:")
            pretty_print_table(results["table"])

    if json_file is not None:
        write_json(results, json_file)

    if plot_det is not None or plot_dist is not None:
        import plot
        if plot_det is not None:
            if not silent:
                print("Plotting DET curve graph")
            plot.plot_det(group.table(), log, plot_det)
        if plot_dist is not None:
            if not silent:
                print("Plotting distance vector distribution graph")
            vectors = [d.vector(normalize_graph) for d in group.data]
            plot.plot_dist(vectors, ground_truth, plot_dist)


def to_indices(flags):
    indices = []
    for i, v in enumerate(flags):
        if v:
            indices.append(i)
    return indices


def expand_dir(dir):
    files = os.listdir(dir)
    return [os.path.join(dir, f) for f in files]


def pretty_print_table(table):
    print("distance", "genuine", "true_positive", "false_positive",
          "true_pos_rate", "false_pos_rate", "false_neg_rate", sep="\t")
    for row in table:
        print(
            str(row["distance"]),
            str(row["genuine"]),
            str(row["true_positive"]),
            str(row["false_positive"]),
            str(row["true_pos_rate"]),
            str(row["false_pos_rate"]),
            str(row["false_neg_rate"]),
            sep="\t"
        )


def write_json(data, file):
    with open(file, "w") as f:
        json.dump(data, f, sort_keys=True, indent=4)


main()
