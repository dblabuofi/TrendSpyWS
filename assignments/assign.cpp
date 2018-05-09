// Example program
#include <iostream>
#include <string>

using namespace std;

void assignment1(int* a, int len) {
	int cc, odd = 0, even = 0;
	
	cout<<"Odd: " << odd <<endl;
	
	for (int i = 0; i < len; i++) {
		if (i % 2 == 1)
			odd += a[i];
		if (i % 2 == 1)
			even *= a[i];
	}
	
}

/*
int main()
{
    int a[] = {1,2,3,4,5};
    assignment1(a, 5);
  
}
*/
/**
*	2	even=0	even=1
*	4	i<=a.length	i<a.length
*	7	i%2==1	i%2==0
*	MISSING	System.out.print(even)
**/
/*
vertices
"assignment1 => [0-DECL-a, 1-ASSIGN-odd=0, 2-ASSIGN-even=0, 3-CALL-System.out.println("Odd: "+odd), 4-ASSIGN-i=0, 5-CTRL-i<=a.length, 6-ASSIGN-i++, 7-CTRL-i%2==1, 8-ASSIGN-odd+=a[i], 9-CTRL-i%2==1, 10-ASSIGN-even*=a[i]]"
void assignment1(int* a, int len) {
	int odd = 0, even = 0;
	cout<<"Odd: " << odd <<endl;
	for (int i = 0; i <= len; i++) {
		if (i % 2 == 1)
			odd += a[i];
		if (i % 2 == 1)
			even *= a[i];
	}
}
*/