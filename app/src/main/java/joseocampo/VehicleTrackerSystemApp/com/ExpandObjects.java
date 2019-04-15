package joseocampo.VehicleTrackerSystemApp.com;
public class ExpandObjects {
    private String atributte01;
    private String atributte02;
    private int NumDraw1,NumDraw2;
    public ExpandObjects(String atributte01, String atributte02, int NumDraw1,int NumDraw2 ){
        super();
        this.atributte01 = atributte01;
        this.atributte02 = atributte02;
        this.NumDraw1 = NumDraw1;
        this.NumDraw2 = NumDraw2;

    }
    public String getAtributte01() {
        return atributte01;
    }
    public String getAtributte02() {
        return atributte02;
    }
    public int getNumDraw1() {
        return NumDraw1;
    }
    public int getNumDraw2() {
        return NumDraw2;
    }

}
