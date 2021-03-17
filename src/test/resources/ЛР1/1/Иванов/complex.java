public class complex {
    private double re, im;
    
    complex(){
        re = 0;
        im = 0;
    }
    
    complex(double in_re, double in_im){
        re = in_re;
        im = in_im;
    }
   
    public void set(double in_re, double in_im){
        re = in_re;
        im = in_im;
    }
    
    public double abs(){
        return Math.sqrt(re*re+im*im);
    }
    
    public double arg(){
        if (re==0 && im==0){
            return -1000;
        }
        else if(re>=0&&im>=0||re>=0&&im<=0){
            return Math.toDegrees(Math.atan(im/re));
        }
        else if(re<=0){
            if(im>=0) return Math.toDegrees(Math.atan(im/re)+Math.PI);
            else if (im<=0) return Math.toDegrees(Math.atan(im/re)-Math.PI);
        }
        return -1000;
    }
    
    public void add(complex n){
        re += n.re;
        im += n.im;
    }
    
    public void sub(complex n){
        re -= n.re;
        im -= n.im;
    }
    
    @Override
    public String toString(){
        return re+"+i*("+im+")";
    }
    
    public String show(){
        return re+"+i*("+im+")";
    }
}
