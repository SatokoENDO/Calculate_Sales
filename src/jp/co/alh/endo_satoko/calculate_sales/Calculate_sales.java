package jp.co.alh.endo_satoko.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Calculate_sales {
	public static void main(String[] args) {

		//支店コードをkey、支店名をvalueにしたマップ
		HashMap<String,String> branchmap = new HashMap<String,String>();
		//商品コードをkey、商品名をvalueにしたマップ
		HashMap<String,String> commonditymap = new HashMap<String,String>();
		//支店コードをkey、売り上げをvalueにしたマップ
		HashMap<String,String> branchsalemap = new HashMap<String,String>();
		//商品コードをkey、売り上げをvalueにしたマップ
		HashMap<String,String> commonditysalemap = new HashMap<String,String>();


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

			}
			System.out.println(branchmap.entrySet());
		}catch(IOException e){
			System.out.println(e);
		}


		File commondity= new File(args[0]);
		if(args.length != 1){
			System.out.println("商品定義ファイルが存在しません");
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
			}
			System.out.println(commonditymap.entrySet());
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
		ArrayList<String> chosenlist = new ArrayList<String>();
		for(int i = 0; i<filelist.length;i++){
			filelist[i].getName();
			if(filelist[i].getName().length()==12&&filelist[i].getName().endsWith(".rcd")){
				chosenlist.add(filelist[i].getName());;
			}
		}
		//System.out.println(chosenlist);






	}
}


