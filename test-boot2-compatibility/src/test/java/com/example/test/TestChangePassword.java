package com.example.test;



public class TestChangePassword {

    private static final String letterArray [] = {
            "a","b","c","d","e","f","g",
            "h","i","j","k","l","m","n",
            "o","p","q","r","s","t","u",
            "v","w","x","y","z"
    };
    private static final int numberArray [] = {0,1,2,3,4,5,6,7,8,9};

    public static void main(String ...args){
        StringBuilder s = new StringBuilder();
        for (int i=0;i<3;i++){
            if(i==0){
                s.append(letterArray[randomNum(26)].toUpperCase());
            }else {
                s.append(letterArray[randomNum(26)]);
            }

        }
        s.append(".");
        for (int j=0;j<3;j++){
            s.append(letterArray[randomNum(26)]);
        }
        s.append("-");
        for (int k=0;k<2;k++){
            s.append(numberArray[randomNum(10)]);
        }
        System.out.println(s.toString());
    }

    private static int randomNum(int max){
        return (int)(max * Math.random());
    }
}
