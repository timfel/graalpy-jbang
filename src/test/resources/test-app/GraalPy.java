//REPOS central,id=file:///home/tim/.m2/repository/
//DEPS org.graalvm.python:python-language:23.1.0
//DEPS org.graalvm.python:python-resources:23.1.0
//DEPS org.graalvm.python:python-launcher:23.1.0
//DEPS org.graalvm.python:graalpy-jbang:1.0-SNAPSHOT
//SOURCES VirtualFileSystem.java
//PIP termcolor
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.io.IOAccess;

public class GraalPy {
    private static final String VENV_PREFIX = "/vfs/venv";
    private static final String HOME_PREFIX = "/vfs/home";

    public static Context getContext() {
        VirtualFileSystem vfs = new VirtualFileSystem();
        Context context = Context.newBuilder()
            // set true to allow experimental options
            .allowExperimentalOptions(false)
            // deny all privileges unless configured below
            .allowAllAccess(false)
            // allow access to the virtual and the host filesystem, as well as sockets
            .allowIO(IOAccess.newBuilder()
                            .allowHostSocketAccess(true)
                            .fileSystem(vfs)
                            .build())
            // allow creating python threads
            .allowCreateThread(true)
            // allow running Python native extensions
            .allowNativeAccess(true)
            // allow exporting Python values to polyglot bindings and accessing Java from Python
            .allowPolyglotAccess(PolyglotAccess.ALL)
            // choose the backend for the POSIX module
            .option("python.PosixModuleBackend", "java")
            // equivalent to the Python -B flag
            .option("python.DontWriteBytecodeFlag", "true")
            // equivalent to the Python -v flag
            .option("python.VerboseFlag", System.getenv("PYTHONVERBOSE") != null ? "true" : "false")
            // log level
            .option("log.python.level", System.getenv("PYTHONVERBOSE") != null ? "FINE" : "SEVERE")
            // equivalent to setting the PYTHONWARNINGS environment variable
            .option("python.WarnOptions", System.getenv("PYTHONWARNINGS") == null ? "" : System.getenv("PYTHONWARNINGS"))
            // print Python exceptions directly
            .option("python.AlwaysRunExcepthook", "true")
            // Force to automatically import site.py module, to make Python packages available
            .option("python.ForceImportSite", "true")
            // The sys.executable path, a virtual path that is used by the interpreter to discover packages
            .option("python.Executable", vfs.resourcePathToPlatformPath(VENV_PREFIX) + (VirtualFileSystem.isWindows() ? "\\Scripts\\python.exe" : "/bin/python"))
            // Set the python home to be read from the embedded resources
            .option("python.PythonHome", vfs.resourcePathToPlatformPath(HOME_PREFIX))
            // Do not warn if running without JIT. This can be desirable for short running scripts
            // to reduce memory footprint.
            .option("engine.WarnInterpreterOnly", "false")
            .build();
        return context;
    }

    public static void main(String[] args) {
        try (Context context = getContext()) {
            switch (args.length) {
                case 0:
                    context.eval("python", "import site; site._script()");
                    break;
                case 1:
                    context.eval("python", args[0]);
                    break;
                default:
                    throw new IllegalArgumentException("The main() helper only takes 0-1 arguments.");
            }
        } catch (PolyglotException e) {
            if (e.isExit()) {
                System.exit(e.getExitStatus());
            } else {
                throw e;
            }
        }
    }
}
