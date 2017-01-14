package cn.jingling.lib.file;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.Environment;

public class CopyOfLog {
	static File SDFile;
	static File logFile;
	static FileOutputStream fos;
	
	public CopyOfLog(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			try {
				SDFile = android.os.Environment.getExternalStorageDirectory();  
				logFile = new File(SDFile.getAbsolutePath()+ File.separator + "myMatrix.txt");
				if (!logFile.exists()) {
					logFile.createNewFile();  
				}
				fos = new FileOutputStream(logFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}

	public void writeArrayAsMatrix(int[] origPixel,int lineLenth){
		if(origPixel ==null || origPixel.length==0)
			return;
		int i=0;
		try {
			while(i<origPixel.length){
				fos.write(Integer.toString(origPixel[i]).getBytes());
				
				if((((i+1) % lineLenth) ==0) || ((i+1)==origPixel.length))
					fos.write('\n');
				else
					fos.write(',');
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	public void add(String log){		
//			log = '\n'+getDate()+"  "+log;			
//			try {
//				if(fos!=null)
//				fos.write(log.getBytes());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	}
//	
//	private String getDate(){
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = new Date();
//		return sdf.format(date);
//	}

}
