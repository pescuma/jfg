/**
 *        (c) 2007-2010 IKOffice GmbH
 *
 *        http://www.ikoffice.de
 */
package de.ikoffice.widgets;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import de.ikoffice.util.FontsUtils;

/**
 * SplitButton
 * 
 * @author       junhuang   huangjun78@gmail.com
 * @version      1.0
 */
public class SplitButton extends Button {
    
	private final static int DEFAULT_SPACES = 21;
    private final static String EMPTY_SPACE = FontsUtils.getSpaceByWidth(DEFAULT_SPACES);
    private final static Color COLOR_WIDGET_NORMAL_SHADOW = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    private final static Color COLOR_WIDGET_HIGHLIGHT_SHADOW = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
    private final static Color COLOR__BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
    
    private List<SplitButtonSelectionListener> listeners = new LinkedList<SplitButtonSelectionListener>();
    
    private int x1 = -1;
    private int y1 = -1;
    private int x2 = -1;
    private int y2 = -1;
    private Menu menu;
    
    public SplitButton(Composite parent, int style) {
        super(parent, SWT.PUSH);
        setText("");
        super.addPaintListener(new PaintListener() {
            
            @Override
            public void paintControl(PaintEvent e) {
                // draw the split line and arrow
            	
            	Color oldForeground = e.gc.getForeground();
                Color oldBackground = e.gc.getBackground();
                
                int x = 0;
                int y = 0;
                int width = getBounds().width;
                int height = getBounds().height;
                
				x1 = x + width - 20;
				y1 = y + 4;
                x2 = x + width;
				y2 = y + height;
                
                e.gc.setForeground(COLOR_WIDGET_NORMAL_SHADOW);
                e.gc.setBackground(COLOR_WIDGET_NORMAL_SHADOW);
                e.gc.setLineWidth(1);
                e.gc.drawLine(x + width-20, y+6, x + width -20, y + height-6);
                
                e.gc.setForeground(COLOR_WIDGET_HIGHLIGHT_SHADOW);
                e.gc.setBackground(COLOR_WIDGET_HIGHLIGHT_SHADOW);
                e.gc.setLineWidth(1);
                e.gc.drawLine(x + width-19, y+6, x + width -19, y + height-6);

                e.gc.setForeground(COLOR__BLACK);
                e.gc.setBackground(COLOR__BLACK);
                e.gc.fillPolygon(new int[] {x + width-15, y + height/2-1, 
                		x + width-8, y + height/2-1, 
                		x + width-12, y + height/2+3}); 
                
                e.gc.setForeground(oldForeground);
                e.gc.setBackground(oldBackground);
                
//                e.gc.drawImage(ARROW_DOWN, e.x + e.width-15, e.y + e.height/2-2);  
            }
        });
        super.addListener(SWT.MouseDown, new Listener() {
            
            @Override
            public void handleEvent(Event event) {
                if (isShowMenu(event.x, event.y)) {
                    
                    for (SplitButtonSelectionListener listener : listeners) {
                        if (!listener.showMenu()) {
                            return;
                        }
                    }
                    Button button = (Button) event.widget;
                    Rectangle rect = button.getBounds();
                    Point p = button.toDisplay(rect.x, rect.y + rect.height);
                    getMenu().setLocation(p.x-rect.x, p.y-rect.y);
                    getMenu().setVisible(true);
                     
                } else {
                    for (SplitButtonSelectionListener listener : listeners) {
                        listener.buttonSelected();
                    }
                }
            }
        });
        menu = new Menu (getShell(), SWT.POP_UP);;
    }
    
    private boolean isShowMenu(int x, int y) {
        return x>=x1 && y>=y1 && x<=x2 && y<=y2;
    }
    
    public void addSplitButtonSelectionListener(SplitButtonSelectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    protected void checkSubclass() {
     // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public void setText(String string) {
        if (string != null) {
            super.setText(string + EMPTY_SPACE);
        } 
    }

    @Override
    public String getText() {
        return super.getText().trim();
    }

}
