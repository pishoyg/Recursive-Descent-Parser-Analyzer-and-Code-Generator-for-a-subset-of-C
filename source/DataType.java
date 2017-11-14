
public enum DataType
{
		integer, real;
		public int getSize() throws Exception
		{
			switch(this)
			{
			case integer: return 2;
			case real: return 4;
			default: throw new Exception("DataType: " + this.name() +
					" has unrecognized size");
			}
		}
}
