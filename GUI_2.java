import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;


public class GUI_2 implements ActionListener {

    private JFrame frame;
    private JLabel label;
    private JButton button;
    private String latestIp;

    public GUI_2(){
        frame = new JFrame();
        frame.setTitle("What is your IP?");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon("./icon.png");
        frame.setIconImage(icon.getImage());

        button = new JButton("Update");
        button.addActionListener(this);

    }

    public void makeGui(String ipaddr){
        JPanel panel = new JPanel();
        label = new JLabel(ipaddr);
        label.setFont(new Font("Arial", Font.PLAIN, 30));
        label.setForeground(Color.BLUE);

        panel.add(label);

        panel.add(button);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws AWTException {

        GUI_2 gui = new GUI_2();
        gui.makeGui(gui.result());
        gui.autoRefresh();
    }

    public void autoRefresh(){
        GUI_2 gui = new GUI_2();
        while(true){
            try{
                Thread.sleep(1000 * 60);
                label.setText(gui.result());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String result(){
        try{
            HttpRequest req = HttpRequest
                    .newBuilder(URI.create("https://api.my-ip.io/ip.json"))
                    .build();

            BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
            HttpResponse<String> response = HttpClient.newBuilder().build().send(req, bodyHandler);
            System.out.println(response.body());

            String data = response.body();

            ObjectMapper mapper = new ObjectMapper();

            try{
                JsonNode node = mapper.readTree(data);

                String ip = node.get("ip").textValue();

                System.out.println(ip);

//                System.out.println(latestIp);

                if(latestIp == null){
                    System.out.println("一番最初");
                    latestIp = ip;
                    return ip;
                }else if(latestIp.equals(ip)){
                    System.out.println("IPが同じです");
                    return ip;
                }else{
                    System.out.println("IPが異なります");
                    latestIp = ip;
                    notification(ip);
                    return ip;
                }

            }catch (Exception e){
               e.printStackTrace();
               return "Plz Refresh";
            }

        }catch (Exception e){
            System.out.println(e);
            return "Plz Refresh";
        }
    }

    public void notification(String ipaddress) throws AWTException{
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "Tray!");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Tray Icon!");
        tray.add(trayIcon);
        trayIcon.displayMessage("What is my IP?", "IPアドレスが変わりました! " + ipaddress, TrayIcon.MessageType.WARNING);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        label.setText(result());
    }
}
