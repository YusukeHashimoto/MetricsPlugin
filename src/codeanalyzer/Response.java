package codeanalyzer;

public class Response {
	private String classname;
	private String methodname;

	Response(String classname, String methodname) {
		this.classname = classname;
		this.methodname = methodname;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Response))
			return false;
		Response r = (Response) o;
		return classname.equals(r.classname) && methodname.equals(r.methodname);
	}

	public String getClassname() {
		return classname;
	}

	public String getMethodname() {
		return methodname;
	}

	@Override
	public int hashCode() {
		return classname.hashCode() * (methodname.hashCode() + 13);
	}
}
