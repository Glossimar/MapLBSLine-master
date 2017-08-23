package com.bignerdranch.android.maplbsline.Tools;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class ClientSocket{

//    private Socket s;
//    private String phoneNum;
//    private String password;
//    private int functions;
    private static boolean result = false;
    private static List<Double> latResult = null;
    private static List<Double> lonResult = null;
    private static List<LatLng> locationResult  = null;
    private static String IPAddress = "123.207.31.42";
    private static int portNumber = 9999;
//
//    public static final int getPhoneFromServer = 1;
//    private final int getPasswordFromServer = 2;
//    private final int findCertenClientTrack = 3;
//    private final int getCertenClientDay1BufferLat = 4;
//    private final int sendClientInfo = 5;
//    private final int getCertenClientDay1Bufferlng = 6;
//    private final int updateDay1AndDay2 = 7;
//    private final int checkNumFromServer = 8;
//    private final int checkPasswordFromServer = 9;
//    private final int addNewClient = 10;
//    public static final int addNewTrackLatitude = 11;
//    public static final int addNewTrackLongitude = 12;
//    public static final int getCertenClientTrack1 = 13;
//    public static final int searchClient = 14;


    public static boolean getPhoneFromServer(final Socket s, final String phoneNum) throws IOException {

        final String[] a = new String[1];
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                try {
                    //        String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
//        File f = new File(fileDirectory + "PhoneNumber.txt");
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
//        BufferedWriter bw = new BufferedWriter(new FileWriter(phoneNum)
                    String line;
                    result = false;
//        List<String> phoneNumList = new ArrayList<>();
                    while ((line = br.readLine()) != null) {
//            phoneNumList.add(line);
                        Log.d(TAG, "run: line" +line);
                        if (line.equals(phoneNum)) {
                            result = true;
                            break;
                        }
                    }
                    Log.d(TAG, "run:" + line);
                    a[0] = "ok";
                } catch (Exception e) {
                    e.printStackTrace();
                }

//            }
//        }).start();
        while (true) {
            if (a[0] == "ok") {
                break;
            }
        }
        return result;
    }

    public static boolean getPasswordFromServer(final Socket s, final String phoneNum, final String password){
        final String[] a = new String[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File f = new File(fileDirectory + "Password.txt");
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
//        BufferedWriter bw = new BufferedWriter(new FileWriter(phoneNum)
                    String line;
                    result = false;
//                    List<String> phoneNumList = new ArrayList<>();
                    while ((line = br.readLine()) != null) {
//            phoneNumList.add(line);
                        Log.d(TAG, "getInfoFromServer: " + line);
                        if (line.equals(phoneNum+password)) {
                            Log.d(TAG, "run: " + password + phoneNum);
                            result = true;
                            break;
                        }
                    }
                    a[0] = "ok";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            if (a[0] == "ok") {
                break;
            }
        }
        Log.d(TAG, "getPasswordFromServer: " + result);
        return result;
    }

    public static void checkNameFromServer(final String phoneNumber, SetNameListener listener){
        final String[] a =new String[2];
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    Log.d(TAG, "checkNumFromServer: 0000000000000000000000000");
                    os.write("Check client name\n".getBytes());
                    a[0] = getNameFromServer(phoneNumber, s);
                    s.close();
                    a[1] = "ok";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true){
            if (a[1] == "ok") {
                Log.d(TAG, "checkNumFromServer: 888888");
                break;
            }
        }
        Log.d(TAG, "checkNumFromServer: 22222222222");
        listener.onFinish(a[0]);
    }

    public static String getNameFromServer(final String phoneNum, Socket s){
        final String[] a = new String[1];
        String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                Log.d(TAG, "getInfoFromServer: " + line);
                if (line.substring(0, 11).equals(phoneNum)) {
                    a[0] = line.substring(11);    // a[1]是返回的昵称
                    Log.d(TAG, "run: " + a[0] + phoneNum);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPasswordFromServer: " + result);
        return a[0];
    }

    public static List<FriendsInfo> getFriendsFromServer(Socket s){
        List<FriendsInfo> friendsInfoList = new ArrayList<>();
        String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                Log.d(TAG, "getInfoFromServer: " + line);
                String phoneNumber = line.substring(0,11);
                String name = line.substring(11);
                FriendsInfo info = new FriendsInfo(name, phoneNumber);
                friendsInfoList.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPasswordFromServer: " + result);
        return friendsInfoList;
    }

    public static boolean getAddCResFromServer(){
        final String[] a = new String[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line == "true") {
                            result = true;
                            break;
                        }
                    }
                    a[0] = "ok";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            if (a[0] == "ok") {
                break;
            }
        }
        return result;
    }

    public static void findCertenClientTrack(final Socket s, final String phoneNum){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    String line = phoneNum + "Day1";
                    bw.write(line);
                    bw.newLine();
                    bw.flush();
                    s.shutdownOutput();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static List<Double> getCertenClientDay1BufferLat(final Socket s){
        new Thread(new Runnable() {
            @Override
            public void run() {
              try {
                  BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                  List<Double> latitudeList = new ArrayList<>();
//        List<String> longitudeList = new ArrayList<>();
                  String line = null;
                  if (!((line = br.readLine()) == null)) {
                      while ((line = br.readLine()) != null) {
                          latitudeList.add(Double.parseDouble(line));
                      }
                      latResult = latitudeList;
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              }
            }
        }).start();
        return latResult;
    }

    public static void sendClientInfo (final Socket s, final String phoneNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    bw.write(phoneNumber);
                    bw.newLine();
                    bw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static List<Double> getCertenClientDay1Bufferlng(final Socket s){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
//        List<Double> longitudeList = new ArrayList<>();
                    List<Double> longitudeList = new ArrayList<>();
                    String line = null;
                    if (!((line = br.readLine()) == null)) {

                    } else {
                        while ((line = br.readLine()) != null) {
                            longitudeList.add(Double.parseDouble(line));
                        }
                        lonResult = longitudeList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return lonResult;
    }

    public static void updateDay1AndDay2(final String phoneNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Update Day1\n" + phoneNumber).getBytes());
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean checkNumFromServer(final String phoneNumber){
        final String[] a = {null};
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    Log.d(TAG, "checkNumFromServer: 0000000000000000000000000");
                    os.write("Check phone number".getBytes());

                    result = getPhoneFromServer(s, phoneNumber);
                    s.close();
                    a[0] = "ok";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true){
            if (a[0] == "ok") {
                Log.d(TAG, "checkNumFromServer: 888888");
                break;
            }
        }
        Log.d(TAG, "checkNumFromServer: 22222222222");
        return result;
    }

    public static boolean checkPasswordFromServer(final String phoneNumber, final String password) {
        final Boolean[] passwordResult = {false};
        final String[] a = {null};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    10.13.134.168
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write("Check password".getBytes());
                    passwordResult[0] = getPasswordFromServer(s, phoneNumber, password);
                    Log.d(TAG, "run: checkPasswordFromServercheckPasswordFromServercheckPasswordFromServer");
                    a[0] = "ok";
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.d("ClientSocketTools", "checkPasswordFromServer: " + passwordResult[0]);
        while (true){
            if (a[0] == "ok") {
                Log.d(TAG, "checkNumFromServer: 888888");
                break;
            }
        }
        return passwordResult[0];
    }

    public static void addNewClient(final String phoneNum, final String password, final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
//                    os.write("Add new client\n".getBytes());
                    os.write(("Add new client\n" + phoneNum + "\n" + phoneNum + password + "\n" + phoneNum + name + "\n").getBytes());
//                    os.write((phoneNum + password + "\n").getBytes());
//                    os.write((phoneNum + name + "\n").getBytes());
                    s.shutdownOutput();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public static void addNewTrackLatitude(final List<Double> latitudeList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> latitudeString= new ArrayList<>();
                for (int i = 0; i < latitudeList.size(); i++) {
                    latitudeString.add(latitudeList.get(i).toString());
                }

                try {
                    Socket s = new Socket("1172.18.39.227", portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write("Add new location of latitude".getBytes());
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    for (int i = 0; i < latitudeString.size(); i ++) {
                        bw.write(latitudeString.get(i));
                        bw.newLine();
                        bw.flush();
                    }
                    s.shutdownOutput();
//                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void addNewTrackLongitude(final List<Double> longitudeList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> longitudeString= new ArrayList<>();
                for (int i = 0; i < longitudeString.size(); i++) {
                    longitudeString.add(longitudeList.get(i).toString());
                }

                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write("Add new location of longitude".getBytes());
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    for (int i = 0; i < longitudeString.size(); i ++) {
                        bw.write(longitudeString.get(i));
                        bw.newLine();
                        bw.flush();
                    }
                    s.shutdownOutput();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static List<LatLng> getCertenClientTrack1(final String phoneNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write("Get certen client latitude of track1".getBytes());
                    findCertenClientTrack(s, phoneNumber);
                    List<Double> latitudeList = getCertenClientDay1BufferLat(s);
//                    s.close();
                    s = new Socket("172.18.39.227", portNumber);
                    os.write("Get certen client longitude of track1".getBytes());
                    findCertenClientTrack(s, phoneNumber);
                    List<Double> longitudeList = getCertenClientDay1Bufferlng(s);

                    List<LatLng> locationList = new ArrayList<>();
                    for (int i = 0; i < longitudeList.size(); i++) {
                        LatLng latlng = new LatLng(latitudeList.get(i), longitudeList.get(i));
                        locationList.add(latlng);
                    }
                    locationResult= locationList;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return locationResult;
    }

    public static void checkFriendsFromServer(final String phoneNumber, final SetNameListener listener){

        List<FriendsInfo> friendsInfoList;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    Log.d(TAG, "checkNumFromServer: 0000000000000000000000000");
                    os.write(("Check friends information\n" + phoneNumber + "\n").getBytes());
//                    os.write(().getBytes());
                    List<FriendsInfo> friendsInfoList = getFriendsFromServer(s);
                    s.close();
                    listener.onFinish(friendsInfoList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.d(TAG, "checkNumFromServer: 22222222222");
    }

    public static void addNewFriends(final String myPhoneNumber, final String friendsInfo){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    Log.d(TAG, "checkNumFromServer: 0000000000000000000000000");
                    os.write(("Add new friends\n" + myPhoneNumber + "\n" + friendsInfo + "\n").getBytes());
//                    os.write((myPhoneNumber + "\n").getBytes());
//                    Log.d(TAG, "run: " + myPhoneNumber + friendsInfo);
//                    os.write((friendsInfo + "\n").getBytes());
                    s.shutdownOutput();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.d(TAG, "checkNumFromServer: 22222222222");
    }

    public static boolean addLocationLatitude(final String phoneNum, final String date, final List<LatLng> latLngList, final String ifUpdate){
        final String[] a = new String[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Add new Location Latitude\n" + phoneNum + "\n" + date + "\n" + ifUpdate +"\n").getBytes());
//                    os.close();
                    for (int i = 0; i < latLngList.size(); i ++) {
                        Log.d("addLocationLongitude", "run: " + latLngList.size());
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                        bw.write((latLngList.get(i).latitude) + "");
                        bw.newLine();
                        bw.flush();
                    }
                    s.shutdownOutput();

//                    s.close();
                    a[0] = "ok";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            if (a[0] == "ok") break;
        }
        return true;
    }

    public static boolean addLocationLongitude(final String phoneNum, final String date, final List<LatLng> latLngList){
        final String[] a = new String[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Add new location longitude\n" + phoneNum + "\n" + date + "\n" + "false\n" ).getBytes());

                    for (int i = 0; i < latLngList.size(); i ++) {
                        Log.d("addLocationLongitude", "run: " + latLngList.size());
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                        bw.write((latLngList.get(i).longitude) + "");
                        bw.newLine();
                        bw.flush();
                    }
                    s.shutdownOutput();
//                    s.close();
                    a[0] = "ok";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            if (a[0] == "ok") break;
        }
        return true;
    }

    public static void getLocationLatitudeFromServer(final String date, final String phoneNumber
            , final SetNameListener listener){
        final List<Double> lattitudeList = new ArrayList<>();
        String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Get location latitude\n" + phoneNumber + "\n" + date + "\n").getBytes());
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        lattitudeList.add(Double.parseDouble(line));
                    }
                    listener.onLocationGetFinish(lattitudeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void getLocationLongitudeFromServer(final String date, final String phoneNumber
            , final SetNameListener listener){
        final List<Double> longitudeList = new ArrayList<>();
        String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Get location longitude\n" + phoneNumber + "\n" + date + "\n").getBytes());
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        Log.d(TAG, "getInfoFromServer: " + line);
                        longitudeList.add(Double.parseDouble(line));
                    }
                    listener.onLocationGetFinish(longitudeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


//    public static boolean searchClient(final String phoneNumber) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Socket s = new Socket(IPAddress, portNumber);
//                    OutputStream os = s.getOutputStream();
//                    os.write("Search client".getBytes());
//                    sendClientInfo(s, phoneNumber);
//                    InputStream is = s.getInputStream();
//                    byte[] bys = new byte[1024];
//                    int len = is.read(bys);
//                    result =Boolean.parseBoolean(new String(bys, 0, len));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        return result;
//    }
//

}
