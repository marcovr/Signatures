import os
import sys
import getopt
import evaluation


def usage():
    print("usage: %s (-g INT | -G FLAGS) (-r INT | -R FLAGS) [-t -h] INPUT..." % sys.argv[0])
    print()
    print("-g INT      number indicating amount of genuine signatures (0 to N)")
    print("-G FLAGS    flags specifying genuine signatures (1 = genuine, 0 = forgery)")
    print("   Notice:  only non-reference signatures are considered (as list 0 to K).")
    print()
    print("-r INT      number indicating amount of reference signatures (0 to N)")
    print("-R FLAGS    flags specifying reference signatures (1 = reference, 0 = non r.)")
    print()
    print("-t          print table")
    print("-h, --help  show this")
    print()
    print("INPUT       directory containing input files or list of input files")


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'tg:G:r:R:h', ['help'])
    except getopt.GetoptError:
        usage()
        sys.exit(2)

    reference = genuine = None
    show_table = False

    for opt, arg in opts:
        if opt in ('-h', '--help'):
            usage()
            sys.exit(2)
        elif opt == '-t':
            show_table = True
        elif opt == '-g':
            genuine = [1] * int(arg)
        elif opt == '-G':
            genuine = map(int, arg)
        elif opt == '-r':
            reference = range(0, int(arg))
        elif opt == '-R':
            reference = to_indices(map(int, arg))
        else:
            usage()
            sys.exit(2)

    if len(args) == 0 or reference is None or genuine is None:
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
    for file in files:
        vector = get_vector(file, reference)
        vectors.extend(vector)
        truth = pad_flags(genuine, len(vector))
        ground_truths.extend(truth)

    table = evaluation.create_table(vectors, ground_truths)
    print(evaluation.get_eer(table))

    if show_table:
        print()
        for row in table:
            print("\t".join(map(str, row)))


def get_vector(file, references):
    matrix = evaluation.read_matrix(file)
    return evaluation.extract_vector(matrix, references)


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
