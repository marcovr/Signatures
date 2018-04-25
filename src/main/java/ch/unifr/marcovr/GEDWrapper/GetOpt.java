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

            // check for option
            if (arg.charAt(0) != '-') {
                args.add(arg);
                continue;
            }

            char[] chars = arg.substring(1).toCharArray();
            for (char c : chars) {
                // check if option is expected
                int j = options.indexOf(c);
                if (j < 0) {
                    throw new OptException("invalid option: -" + c);
                }

                // check if option requires argument
                String optArg = null;
                if (options.length() > j + 1 && options.charAt(j + 1) == ':') {
                    if (++i >= argv.length) {
                        throw new OptException("missing argument: -" + c);
                    }
                    optArg = argv[i];
                }

                opts.add(new Option("-" + c, optArg));
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
