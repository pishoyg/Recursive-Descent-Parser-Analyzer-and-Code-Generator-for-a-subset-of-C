public class VariableName {
	String Name;

	final static int MOD = 1000003; // a large prime
	final static int BASE = 128;

	VariableName(String name) {
		this.Name = name;
	}

	@Override
	public int hashCode() {
		int ans = 0;
		for (char c : this.Name.toCharArray())
			ans = (ans * BASE + c) % MOD;
		return ans;
	}

	@Override
	public boolean equals(Object other) {
		return
			other != null
			&& VariableName.class.isInstance(other)
			&& this.Name.equals(((VariableName) other).Name);
	}
}
