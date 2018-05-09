void derivatives(float[] a) {
	for (int i = 1; i < a.length; i++)
		System.out.print((a[i] * i) + " ");
	if (a.length == 1)
		System.out.print("0 ");
}