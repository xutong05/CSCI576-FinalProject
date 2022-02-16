import javax.swing.*;
import java.awt.*;

public class ListDialog extends JDialog {

    JLabel title;
    JLabel linkName;
    JScrollPane scrollPane;
    JList<ItemName> list;
    DefaultListModel<ItemName> listModel;
    JButton deleteBttn;

    ListDialog(){
        title = new JLabel("List of Link(s) added");
        linkName = new JLabel("(Empty)");
        scrollPane = new JScrollPane();
        list = new JList<>();
        listModel = new DefaultListModel<>();
        deleteBttn = new JButton(new ImageIcon(this.getClass().getResource("/images/delete.png")));
        list.setModel(listModel);
        scrollPane.getViewport().setView(list);

        GridBagLayout gLayout = new GridBagLayout();
        this.setLayout(gLayout);
        GridBagConstraints cTitle = new GridBagConstraints(0,0,2,1,1,0.025,
                GridBagConstraints.CENTER,GridBagConstraints.BOTH,
                new Insets(1,5,1,5),0,0);
        GridBagConstraints cName = new GridBagConstraints(0,1,1,1,0.875,0.025,
                GridBagConstraints.WEST,GridBagConstraints.BOTH,
                new Insets(1,5,1,5),0,0);
        GridBagConstraints cDel = new GridBagConstraints(1,1,1,1,0.125,0.025,
                GridBagConstraints.WEST,GridBagConstraints.BOTH,
                new Insets(1,5,1,5),0,0);
        GridBagConstraints cList =  new GridBagConstraints(0,2,2,1,1,0.95,
                GridBagConstraints.WEST,GridBagConstraints.BOTH,
                new Insets(1,5,10,5),0,0);
        this.add(title,cTitle);
        this.add(linkName,cName);
        this.add(deleteBttn,cDel);
        this.add(scrollPane,cList);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }

    public void addItem(String name, int id){
        listModel.addElement(new ItemName(name,id));
    }
    public void deleteItem(int i){
        listModel.remove(i);
    }
    public void updateItem(String name, int index){
        for(int i=0;i<listModel.getSize();i++){
            if(listModel.get(i).getIndex() == index){
                listModel.get(i).setName(name);
                repaint();
                return;
            }
        }
        listModel.addElement(new ItemName(name,index));
    }



}

class ItemName {
    private String nameLink;
    private int indexLink;
    ItemName(String name, int index){
        nameLink = name;
        indexLink = index;
    }

    public int getIndex(){
        return indexLink;
    }
    public void setName(String name){
        nameLink = name;
    }
    @Override
    public String toString() {
        return nameLink;
    }
}
