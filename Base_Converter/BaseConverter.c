#include <stdio.h>
#include <math.h>
#include <stdlib.h>


int i, decNum, tBase, sBase = 0;


void check_legal(int sBase, int sORt) {
    if (!((sBase>=2) && (sBase<=16))) {
        if (sORt == 0)  
        printf("Invalid source base!");
        
        else 
            printf("Invalid target base!");

        exit(0);
    }

    return;
}


void checkAndAdd(char c, char shift, int delta) {
    int num_with_shift = c - shift + delta;
    if (num_with_shift >= sBase) {
        printf("Invalid input number!");
        exit(0);
    }
    decNum += num_with_shift*pow(sBase,i);
    return;
}


void convertToDec(int base) { 
    char c;

    if ((c = getchar()) != '\n') {
        convertToDec(base);
    
        if ((c<=47) || (c>=58 && c<=96) || (c>=103)) {
            printf("Invalid input number!");
            exit(0);
        }
        
        else {
            if (c>=48 && c<=57) 
                checkAndAdd(c,'0', 0);
            
            if (c>=97 && c<=102) 
                checkAndAdd(c, 'a', 10);
            
            i++;
        }

    }

    return;
}


void rec_mod(int num) {
    int curr_mod;

    if (num==0)
        return;
    
    rec_mod(floor(num/tBase));

    curr_mod = num%tBase;
    if (curr_mod>=10) {
        curr_mod = 'a' + curr_mod - 10;
        printf("%c", curr_mod); 
    }
    else  
        printf("%i", curr_mod);
    
    return;
}


int main(void) {

    printf("enter the source base:\n");
    scanf("%d", &sBase);
    check_legal(sBase, 0); /* 0 means its source */

    printf("enter the target base:\n");
    scanf("%d", &tBase);
    check_legal(tBase, 1); /* 1 means its target */

    printf("enter the number in base %d:\n", sBase);
    getchar();
    convertToDec(sBase);

    printf("The number in base %d is: ", tBase);
    rec_mod(decNum);

    return 0;
}
