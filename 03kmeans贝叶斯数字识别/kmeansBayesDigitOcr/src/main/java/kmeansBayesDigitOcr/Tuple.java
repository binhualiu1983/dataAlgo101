package kmeansBayesDigitOcr;

public class Tuple<T1, T2> {
	public T1 _1;
	public T2 _2;
	
	public Tuple()
	{}
	
	public Tuple(T1 _1, T2 _2)
	{
		this._1 = _1;
		this._2 = _2;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s:%s", String.valueOf(_1), String.valueOf(_2));
	}
	
	

}
