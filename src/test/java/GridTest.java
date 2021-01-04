import com.jsimforest.Grid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GridTest {
    @Test
    public void testGrille() throws Exception {
        Grid myGrid = new Grid(5, 8);
        assertEquals(myGrid.getHeight(),myGrid.getMatrix().size());
        for(int i=0; i< myGrid.getWidth();i++){
            assertEquals(myGrid.getWidth(), myGrid.getMatrix().get(i).size());
        }
    }
}