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

public class Yomikomi {
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

				//支店定義ファイルのエラー処理
				if(! branchname[0].matches("^\\d{3}$")&&branchname[1].matches("[,\n]")){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				else{
					branchmap.put(branchname[0],branchname[1] );
					branchsalemap.put(branchname[0],0L);//0Lにしておかないと0がintとして処理される
				}

			}
			//System.out.println(branchmap.entrySet());
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}


		//商品定義ファイルの読み込み
		try{
			File file = new File(args[0],"commondity.lst");

			if(args.length != 1){
				System.out.println("商品定義ファイルが存在しません");
			}else{

				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String s;
				while((s=br.readLine()) !=null){

					// sをコンマで区切り、商品コードと商品名に分割して、配列commonditynameにぶちこむ
					String [] commondityname = s.split(",");

					//商品定義ファイルのエラー処理
					if(! commondityname[0].matches("[a-zA-Z0-9]{8}$")){
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}else
						commonditymap.put(commondityname[0], commondityname[1]);
					commonditysalemap.put(commondityname[0],0L);
				}
				//System.out.println(commonditymap.entrySet());
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}

		//集計
		File file = new File(args[0]);
		File [] filelist=file.listFiles();
		/*for (int i = 0;i<filelist.length; i++){
			System.out.println(filelist[i]);
		}*/

		//File型のListを作成、配列filelistを変換
		List<File> list=Arrays.asList(filelist);

		//System.out.println(+filelist.length);
		ArrayList<File> chosenlist = new ArrayList<File>();
		for(int i = 0; i<filelist.length;i++){
			filelist[i].getName();
			if(filelist[i].getName().matches("\\d{8}.rcd$")){//数字が8桁で末尾に.rcdを含む

				chosenlist.add(filelist[i]);

				//歯抜けになっている場合の処理
				for(int num =0; num< chosenlist.size(); num++){
					String [] filename = filelist[num].getName().split("\\.");
					int filenumber = Integer.parseInt(filename[0]);
					if(!((num +1) == filenumber)){
						System.out.println("売り上げファイル名が連番になっていません");
						return;
					}
				}
			}

		}
		//System.out.println(chosenlist);

		//売り上げファイルの読み込み;
		for(int i=0; i<chosenlist.size();i++){
			//ループをまわして新しいリストに足していく
			try {
				ArrayList<String> saleslist = new ArrayList<String>();
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

					//売り上げファイルの支店コードが支店定義ファイルに存在しない場合
					if(!branchmap.containsKey(saleslist.get(0)))
					{
						System.out.println(chosenlist.get(i).getName()+"の支店コードが不正です");
					}

					//売り上げファイルの商品コードが商品定義ファイルに存在しない場合
					if(!commonditymap.containsKey(saleslist.get(1)))
					{
						System.out.println(chosenlist.get(i).getName()+"の商品コードが不正です");
					}



				} catch (IOException e) {
					System.out.println(e);
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				System.out.println("予期せぬエラーが発生しました");// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}


		}
		//System.out.println(branchsalemap.entrySet());
		//System.out.println(commonditysalemap.entrySet());


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


				String bsvalue = String.valueOf(bs.getValue());
				if(bsvalue.matches("\\d{10,}")){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
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

		/*支店をソートしたリストのキーと、支店マップのキー部分に支店番号を代入したもの、支店をソートしたリストの中身
		for (Entry<String, Long> bs : branchsortlist){
			System.out.println(bs.getKey()+","+ branchmap.get(bs.getKey())+","+bs.getValue()+"\n");
		}*/

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

				String csvalue = String.valueOf(cs.getValue());
				if(csvalue.matches("\\d{10,}")){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				if(!commonditymap.containsKey(cs.getKey()))
				{
					System.out.println("支店コードが不正です");
				}
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");;
		}finally{
			try {
				coBuffer.close();
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		/*for (Entry<String, Long> cs : commonditysortlist){
			System.out.println(cs.getKey()+","+ commonditymap.get(cs.getKey())+","+cs.getValue()+"\n");

		}*/


		//商品別集計ファイル出力
		File newcommondityfile = new File(args[0],"commondity.out");
		try{
			newcommondityfile.createNewFile();
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}

	}

	private static int parseInt(String string) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

}



