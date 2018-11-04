import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Community {

    public ArrayList<Integer> CCom = new ArrayList<Integer>();
    public String toString()
    {
        String s = "";

        for(int idn : CCom)
        {
            s = s + (idn) + " ";
        }

        s += "\n";

        return s;
    }
}
