package ch.unifr.marcovr.GEDWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulates a limited subset of the GNU getopt functionality.
 */
public class GetOpt {

    private final List<Option> opts;
    private final List<String> args;

    /**
     * Start option parsing.
     *
     * @param argv list of arguments to check
     * @param options option string (see GNU getopt)
     * @throws OptException thrown for invalid option or missing option argument
     */
    public GetOpt(String[] argv, String options) throws OptException {
        opts = new ArrayList<>();
        args = new ArrayList<>();

        for (int i = 0; i < argv.length; i++) {
            String arg = argv[i];
            if (arg.charAt(0) == '-') {
                char[] chars = arg.substring(1).toCharArray();
                for (char c : chars) {
                    int j = options.indexOf(c);
                    if (j >= 0) {
                        if (options.length() > j + 1 && options.charAt(j + 1) == ':') {
                            i++;
                            if (i < argv.length) {
                                opts.add(new Option("-" + c, argv[i]));
                            }
                            else {
                                throw new OptException("missing argument: -" + c);
                            }
                        }
                        else {
                            opts.add(new Option("-" + c));
                        }
                    }
                    else {
                        throw new OptException("invalid option: -" + c);
                    }
                }
            }
            else {
                args.add(arg);
            }
        }
    }

    /**
     * @return list of extracted options
     */
    public List<Option> getOpts() {
        return opts;
    }

    /**
     * @return list of non-option arguments
     */
    public List<String> getArgs() {
        return args;
    }


    /**
     * Class representing an option and an according argument (may be null).
     */
    public class Option {
        public String opt;
        public String arg;

        public Option(String opt) {
            this.opt = opt;
        }

        public Option(String opt, String arg) {
            this.opt = opt;
            this.arg = arg;
        }
    }

    public class OptException extends Exception {
        public OptException(String msg) {
            super(msg);
        }
    }

}
