package jp.co.alh.endo_satoko.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CalculateSales {
		public static void main(String[] args) {

			//支店コードをkey、支店名をvalueにしたマップ
			HashMap<String,String> branchmap = new HashMap<String,String>();
			//商品コードをkey、商品名をvalueにしたマップ
			HashMap<String,String> commonditymap = new HashMap<String,String>();
			//支店コードをkey、売り上げをvalueにしたマップ.売り上げはLong型
			HashMap<String,Long> branchsalemap = new HashMap<String,Long>();
			//商品コードをkey、売り上げをvalueにしたマップ
			HashMap<String,Long> commonditysalemap = new HashMap<String,Long>();


			String origin;
			String[] branchname = null;
			File branch= new File(args[0]);
			if(args.length != 1){
				System.out.println("支店定義ファイルが存在しません");
			}

			//支店定義ファイルの読み込み
			try{
				File file = new File(args[0],"branch.lst");
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);;
				while((origin=br.readLine()) !=null){
					// sをコンマで区切り、支店コードと支店名に分割して、配列branchnameにぶちこむ
					branchname= origin.split(",");
					branchmap.put(branchname[0],branchname[1] );
						branchsalemap.put(branchname[0],0L);//0Lにしておかないと0がintとして処理される

				}
				//System.out.println(branchmap.entrySet());
			}catch(IOException e){
				System.out.println(e);
			}


			File commondity= new File(args[0]);
			if(args.length != 1){
				//System.out.println("商品定義ファイルが存在しません");
			}

			//商品定義ファイルの読み込み
			try{
				File file = new File(args[0],"commondity.lst");
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String s;
				while((s=br.readLine()) !=null){

					// sをコンマで区切り、商品コードと商品名に分割して、配列commonditynameにぶちこむ
					String [] commondityname = s.split(",");
					commonditymap.put(commondityname[0], commondityname[1]);
					commonditysalemap.put(commondityname[0],0L);
				}
				//System.out.println(commonditymap.entrySet());
			}catch(IOException e){
				System.out.println(e);
			}

			//集計
			File file = new File(args[0]);
			File [] filelist=file.listFiles();
			/*for (int i = 0;i<filelist.length; i++){
				System.out.println(filelist[i]);
			}*/

			//String型のListを作成、配列filelistを変換
			List<File> list=Arrays.asList(filelist);

			//System.out.println(+filelist.length);
			ArrayList<File> chosenlist = new ArrayList<File>();
			for(int i = 0; i<filelist.length;i++){
				filelist[i].getName();
				if(filelist[i].getName().length()==12&&filelist[i].getName().endsWith(".rcd")){//ここの正規表現もっとシンプルに
					chosenlist.add(filelist[i]);
					//歯抜けになっている場合
				}
			}
			//System.out.println(chosenlist);

			//売り上げファイルの読み込み
			for(int i=0; i<chosenlist.size();i++){
				//ループをまわして新しいリストに足していく
				ArrayList<String> saleslist = new ArrayList<String>();
				try {
					FileReader fr = new FileReader(chosenlist.get(i));
					BufferedReader br = new BufferedReader(fr);
					String contents;

					//支店の合計金額、商品の合計金額に足していく
					try {
						while((contents = br.readLine()) !=null){
							saleslist.add(contents);;
						}
						long sale = Long.parseLong(saleslist.get(2));

						long branchsales = branchsalemap.get(saleslist.get(0));
						long branchsum = branchsales += sale;

						long commonditysales = commonditysalemap.get(saleslist.get(1));
						long commonditysum = commonditysales += sale;

						//マップに入れる
						branchsalemap.put(saleslist.get(0),branchsum);
						commonditysalemap.put(saleslist.get(1), commonditysum);

					} catch (IOException e) {
						System.out.println("不正です");
						e.printStackTrace();
					}

				} catch (FileNotFoundException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}


			}
			System.out.println(branchsalemap.entrySet());
			System.out.println(commonditysalemap.entrySet());


			//店ごと売り上げ順にソートする。マップをソートするためにリストに格納する。
			List<Map.Entry<String,Long>> branchsortlist = new ArrayList<Map.Entry<String,Long>>(branchsalemap.entrySet());

			Collections.sort(branchsortlist, new Comparator<Map.Entry<String,Long>>(){
				public int compare (Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});

			/*for (Entry<String, Long> bs : branchsortlist) {
				System.out.println(bs.getKey() + " = " + bs.getValue());
			}*/

			//店舗ごとの売り上げ集計ファイル出力
			BufferedWriter brBuffer = null;
			try{
				File branchresult = new File(args[0],"branch.out");
				branchresult.createNewFile();
				FileWriter brWriter = new FileWriter(branchresult);
				brBuffer = new BufferedWriter(brWriter);
				for(Map.Entry<String, Long> bs : branchsortlist){
					brBuffer.write(bs.getKey()+","+ branchmap.get(bs.getKey())+","+bs.getValue()+"\n");
				}
			}catch(IOException e){
				System.out.println(e);
			}finally{
				try {
					brBuffer.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}

			/*for (Entry<String, Long> bs : branchsortlist) {
				System.out.println(bs.getKey() + " = " + bs.getValue());
			}*/

			//支店をソートしたリストのキーと、支店マップのキー部分に支店番号を代入したもの、支店をソートしたリストの中身
			for (Entry<String, Long> bs : branchsortlist){
				System.out.println(bs.getKey()+","+ branchmap.get(bs.getKey())+","+bs.getValue()+"\n");
			}

			//商品ごと売り上げ順にソートする。
			List<Map.Entry<String,Long>> commonditysortlist = new ArrayList<Map.Entry<String,Long>>(commonditysalemap.entrySet());
			Collections.sort(commonditysortlist, new Comparator<Map.Entry<String,Long>>(){
				public int compare (Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});

			//商品ごとの売り上げ集計ファイル出力
			BufferedWriter coBuffer = null;
			try{
				File commondityresult = new File(args[0],"commondity.out");
				commondityresult.createNewFile();
				FileWriter coWriter = new FileWriter(commondityresult);
				coBuffer = new BufferedWriter(coWriter);
				for(Map.Entry<String, Long> cs : commonditysortlist){
					coBuffer.write(cs.getKey()+","+ commonditymap.get(cs.getKey())+","+cs.getValue()+"\n");
				}
			}catch(IOException e){
				System.out.println(e);
			}finally{
				try {
					coBuffer.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}

			for (Entry<String, Long> cs : commonditysortlist){
				System.out.println(cs.getKey()+","+ commonditymap.get(cs.getKey())+","+cs.getValue()+"\n");

			}


			/*for (Entry<String, Long> cs : commonditysortlist) {
				System.out.println(cs.getKey() + " = " + cs.getValue());
			}*/

			//商品別集計ファイル出力
			File newcommondityfile = new File(args[0],"commondity.out");
			try{
				newcommondityfile.createNewFile();
			}catch(IOException e){
				System.out.println(e);
			}

		}
	}

