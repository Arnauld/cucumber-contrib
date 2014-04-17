package cucumber.contrib.grammar;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Source {
    public static Source packageSource(String packageName) {
        return new Source(packageName, Type.Package);
    }

    public static Source classSource(String packageName, String klazzName) {
        return new Source(packageName + ":" + klazzName, Type.Class);
    }

    public static Source methodSource(String methodName, String args) {
        return new Source(methodName + "(" + args + ")", Type.Method);
    }

    public enum Type {
        Package,
        Class,
        Method
    }

    private final String name;
    private final Type type;

    public Source(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Source source = (Source) o;

        return name.equals(source.name)
                && type == source.type;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Source {" + type + " '" + name + "'}";
    }
}
