import javax.swing.JOptionPane;

public class test_complex {
  
    public static void check_res(String input){
        if (input==null){
            JOptionPane.showMessageDialog(null, 
            "Вы завершили выполнение программы",
            "Сообщение",
            JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        
        String input;
        
        input = JOptionPane.showInputDialog(null,
                "Введите действительную часть числа a", 
                "Комплексное число a",
                JOptionPane.QUESTION_MESSAGE);
        
        check_res(input);
        double a_re = Double.parseDouble(input);
        
        input = JOptionPane.showInputDialog(null,
                "Введите мнимую часть числа a", 
                "Комплексное число a",
                JOptionPane.QUESTION_MESSAGE);
        check_res(input);
        double a_im = Double.parseDouble(input);
        
        input = JOptionPane.showInputDialog(null,
                "Введите действительную часть числа b", 
                "Комплексное число b",
                JOptionPane.QUESTION_MESSAGE);
       check_res(input);
       double b_re = Double.parseDouble(input);
       
        input = JOptionPane.showInputDialog(null,
                "Введите мнимую часть числа b", 
                "Комплексное число b",
                JOptionPane.QUESTION_MESSAGE);
        check_res(input);
        double b_im = Double.parseDouble(input);
                
        complex a = new complex(a_re, a_im);
        complex b = new complex(b_re, b_im);
        a.add(b);
        String text ="Модуль числа a+b="+a+" равен "+a.abs()+"\n";
        
        if (a.arg()==-1000){
            text+="Аргумент числа a+b="+a+" не определён"+"\n";
        }
        else{
            text+="Аргумент числа a+b="+a+" равен "+a.arg()+"\n";
        }
        
        a.sub(b);
        a.sub(b);
 
        text+= "Модуль числа a-b="+a+" равен "+a.abs()+"\n";
        
        if (a.arg()==-1000){
            text+="Аргумент числа a-b="+a+" не определён"+"\n";
        }
        else{
            text+="Аргумент числа a-b="+a+" равен "+a.arg()+"\n";
        }
        
        JOptionPane.showMessageDialog(null, text,
                "Результат",JOptionPane.PLAIN_MESSAGE);
    }
    
}
