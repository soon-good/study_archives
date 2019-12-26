package simple.types;

public enum ExtendedOperation implements Operation{
	EXP("^"){
		@Override
		public double apply(double x, double y) {
			return 0;
		}
	},
	REMAINDER("%"){
		@Override
		public double apply(double x, double y) {
			return 0;
		}
	};

	private final String op;

	ExtendedOperation(String op){
		this.op = op;
	}

	@Override
	public String toString() {
		return op;
	}
}
