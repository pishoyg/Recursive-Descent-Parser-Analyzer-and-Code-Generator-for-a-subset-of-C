int main(void)
{
	int x;
	int y[5];
	{
		x = (5 + 3) * (x + 7);
		y[3] = x;
		{
			x = 5;
			x = 5;
		}
		while(x == 5)
			if (y[0] == 6)
				x = 7 + x;
			else { x = 7 - x; }
	}
}

