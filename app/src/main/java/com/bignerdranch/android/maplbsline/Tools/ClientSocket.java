package com.bignerdranch.android.maplbsline.Tools;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ClientSocket{
    private static boolean result = false;
    private static String IPAddress = "123.207.31.42";
    private static int portNumber = 45556;
//172.18.39.227

    private static boolean getPhoneFromServer(final Socket s, final String phoneNum) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            result = false;
            while ((line = br.readLine()) != null) {
                if (line.equals(phoneNum)) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean getPasswordFromServer(final Socket s, final String phoneNum, final String password){


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            result = false;
            while ((line = br.readLine()) != null) {
                Log.d(TAG, "getInfoFromServer: " + line);
                if (line.equals(phoneNum+password)) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPasswordFromServer: " + result);
        return result;
    }

    public static void checkNameFromServer(final String phoneNumber, final SetNameListener listener){
        final String[] a =new String[1];
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write("Check client name\n".getBytes());
                    a[0] = getNameFromServer(phoneNumber, s);
                    s.close();
                    listener.onFinish(a[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String getNameFromServer(final String phoneNum, Socket s){
        final String[] a = new String[1];
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.substring(0, 11).equals(phoneNum)) {
                    a[0] = line.substring(11);    // a[1]是返回的昵称
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPasswordFromServer: " + result);
        return a[0];
    }

    private static List<FriendsInfo> getFriendsFromServer(Socket s){
        List<FriendsInfo> friendsInfoList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
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

    public static boolean checkNumFromServer(final String phoneNumber, final SetNameListener listener){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write("Check phone number".getBytes());
                    result = getPhoneFromServer(s, phoneNumber);
                    listener.onFinish(result);
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return result;
    }

    public static boolean checkPasswordFromServer(final String phoneNumber, final String password, final SetNameListener listener) {
        final Boolean[] passwordResult = {false};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    10.13.134.168
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write("Check password".getBytes());
                    passwordResult[0] = getPasswordFromServer(s, phoneNumber, password);
                    listener.onFinish(passwordResult[0]);
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.d("ClientSocketTools", "checkPasswordFromServer: " + passwordResult[0]);
        return passwordResult[0];
    }

    public static void addNewClient(final String phoneNum, final String password, final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Add new client\n" + phoneNum + "\n" + phoneNum + password + "\n" + phoneNum + name + "\n").getBytes());
                    s.shutdownOutput();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void checkFriendsFromServer(final String phoneNumber, final SetNameListener listener){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Check friends information\n" + phoneNumber + "\n").getBytes());
                    List<FriendsInfo> friendsInfoList = getFriendsFromServer(s);
                    s.close();
                    listener.onFinish(friendsInfoList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void addNewFriends(final String myPhoneNumber, final String friendsInfo){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Add new friends\n" + myPhoneNumber + "\n" + friendsInfo + "\n").getBytes());
                    s.shutdownOutput();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean addLocationLatitude(final String phoneNum, final String date, final List<LatLng> latLngList, final String ifUpdate){
        final int[] i = {0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Add new Location  Latitude\n" + phoneNum + "\n" + date + "\n" + ifUpdate +"\n").getBytes());
                    for (int i = 0; i < latLngList.size(); i ++) {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                        bw.write((latLngList.get(i).latitude) + "");
                        bw.newLine();
                        bw.flush();
                    }
                    s.shutdownOutput();
//                    s.close();
                    i[0] =1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            if (i[0] == 1) break;
        }
        return true;
    }

    public static boolean addLocationLongitude(final String phoneNum, final String date, final List<LatLng> latLngList){
        final int[] i = {0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IPAddress, portNumber);
                    OutputStream os = s.getOutputStream();
                    os.write(("Add new location longitude\n" + phoneNum + "\n" + date + "\n" + "false\n" ).getBytes());

                    for (int i = 0; i < latLngList.size(); i ++) {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                        bw.write((latLngList.get(i).longitude) + "");
                        bw.newLine();
                        bw.flush();
                    }
                    s.shutdownOutput();
//                    s.close();
                    i[0] =1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            if (i[0] == 1) break;
        }
        return true;
    }

    public static void getLocationLatitudeFromServer(final String date, final String phoneNumber
            , final SetNameListener listener){
        final List<Double> lattitudeList = new ArrayList<>();
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
                        longitudeList.add(Double.parseDouble(line));
                    }
                    listener.onLocationGetFinish(longitudeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
