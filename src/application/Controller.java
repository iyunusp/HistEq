package application;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class Controller {
	Mat logo,clon;
	boolean mode=true,ycc=true;
	private int[] Rval=new int[256],Gval=new int[256],Bval=new int[256];
	private int[] Rtemp=new int[256],Gtemp=new int[256],Btemp=new int[256];
	private final CategoryAxis xAxis = new CategoryAxis();
    private final NumberAxis yAxis = new NumberAxis();
    XYChart.Series B = new XYChart.Series(),G = new XYChart.Series(),R = new XYChart.Series();
    XYChart.Series BN = new XYChart.Series(),GN = new XYChart.Series(),RN = new XYChart.Series();
	@FXML
	private BarChart<String, Number> barchart= new BarChart<String, Number>(xAxis, yAxis);
	@FXML
	private ImageView image;
	@FXML
	private Button load;
	@FXML
	private Button convert;
	@FXML
	private void Loadimage(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(application.Main.cpy);
		if (file != null) {
			resetHisto();
		    logo = Imgcodecs.imread(file.getAbsolutePath());
		    clon=logo.clone();
		    if(ycc)
		    	Imgproc.cvtColor(clon, clon, Imgproc.COLOR_BGR2YCrCb);//delete
		    this.image.setImage(mat2Image(logo));
		    barchart.getData().clear();//hapus data barchart
		    assignpixvalue(clon);
		    if(ycc)
		    	Imgproc.cvtColor(clon, clon, Imgproc.COLOR_YCrCb2BGR);//delete
		    convert.setVisible(true);
		    mode=true;
		}
		
		
	}
	@FXML
	private void Convert(){
		if(mode){
			barchart.getData().clear();
			generateHisto(false);
			this.image.setImage(mat2Image(clon));
			load.setVisible(false);
			convert.setText("Reset");
			mode=false;
		}else {
			barchart.getData().clear();
			generateHisto(true);
			this.image.setImage(mat2Image(logo));
			load.setVisible(true);
			convert.setText("Convert");
			mode=true;
		}
	}
	public void initialize(){
		barchart.setTitle("Histogram");
		barchart.setAnimated(false);
	    xAxis.setLabel("RGB");       
	    yAxis.setLabel("Value");
	    image.setFitHeight(300);
	    barchart.setMinHeight(250);
	    if(ycc){
	    	B.setName("Y"); G.setName("Cr"); R.setName("Cb");
	    	BN.setName("YN"); GN.setName("CrN"); RN.setName("CbN");
	    }else{
	    	B.setName("B"); G.setName("G"); R.setName("R");
	    	BN.setName("BN"); GN.setName("GN"); RN.setName("RN");
	    }
		convert.setVisible(false);
	}
	protected void resetSeries(boolean mode) {//true= original false=equalized
		if(mode){
			if(!B.getData().isEmpty()){
				B.getData().clear();
				G.getData().clear();
				R.getData().clear();
			}
		}else{
			if(!BN.getData().isEmpty()){
				BN.getData().clear();
				GN.getData().clear();
				RN.getData().clear();
			}
		}
	}
	protected void generateHisto(boolean mode){//true= original false=equalized
		if(mode){//original picture
			resetSeries(true);
			for (int i = 0; i < 256; i++) {
				B.getData().add(new XYChart.Data(""+i,Bval[i]));
				G.getData().add(new XYChart.Data(""+i,Gval[i]));
				R.getData().add(new XYChart.Data(""+i,Rval[i]));
			}
			barchart.getData().addAll(B,G,R);//show original histogram	
		}else {//equalized picture
			resetSeries(false);
			for (int i = 0; i < 256; i++) {
				BN.getData().add(new XYChart.Data(""+i,Btemp[i]));
				GN.getData().add(new XYChart.Data(""+i,Gtemp[i]));
				RN.getData().add(new XYChart.Data(""+i,Rtemp[i]));
			}
			barchart.getData().addAll(BN,GN,RN);//show equalized histogram
		}
	}
	protected void resetHisto(){
		for (int i = 0; i < 256; i++) {
			Bval[i]=Gval[i]=Rval[i]=0;
			Btemp[i]=Gtemp[i]=Rtemp[i]=0;
		}
	}
	protected void assignvalpixel(Mat pic){//equalizing pixel value to image
		double ukuran=pic.width()*pic.height(),tempB=0,tempG=0,tempR=0;
		double[] ResAll=new double[3],ResB=new double[256],ResG=new double[256],ResR=new double[256];//Res is the result after the normalize
		for (int i = 0; i < 256; i++) {
			tempB+=Bval[i];
			if(!ycc){
				tempG+=Gval[i];
				tempR+=Rval[i];
			}
			/*save the change of B value for example if the total of previous B 
			 * value of 0 is 10000 of 20000(ukuran) it will convert the B value of 0 to 128*/
			ResB[i]=(int)Math.round((tempB/ukuran)*255);
			if(!ycc){
				ResG[i]=(int)Math.round((tempG/ukuran)*255);
				ResR[i]=(int)Math.round((tempR/ukuran)*255);
			}
		}
		for (int i = 0; i < pic.height(); i++) {
			for (int j = 0; j < pic.width(); j++) {
				double[] value=pic.get(i,j);
				ResAll[0]=ResB[(int)value[0]];//change the original B values to equalized B values
				if(!ycc){
					ResAll[1]=ResG[(int)value[1]];//change the original G values to equalized G values
					ResAll[2]=ResR[(int)value[2]];//change the original R values to equalized R values
				}else{
					ResAll[1]=value[1];
					ResAll[2]=value[2];
				}
				pic.put(i, j, ResAll);//change the pixel information from original image to equalized value
				Btemp[(int)ResAll[0]]++;//barchart data
				Gtemp[(int)ResAll[1]]++;//barchart data
				Rtemp[(int)ResAll[2]]++;//barchart data
			}
		}
	}
	protected void assignpixvalue(Mat pic){//assign pixel value to variable to equalizing
		for (int i = 0; i < pic.height(); i++) {
			for (int j = 0; j < pic.width(); j++) {
				double[] value=pic.get(i,j);// get pixel information
				Bval[(int)value[0]]++;//increase the total per B value for example in the picture have 5 data which have value of R 40 +(barchart data)
				Gval[(int)value[1]]++;//increase the total per G value for example in the picture have 1000 data which have value of G 0 +(barchart data)
				Rval[(int)value[2]]++;//increase the total per R value for example in the picture have 1 data which have value of R 255 +(barchart data)
			}
		}
		generateHisto(true);	
        assignvalpixel(pic);
	}
	private Image mat2Image(Mat frame){
		MatOfByte buffer = new MatOfByte();// create a temporary buffer
		Imgcodecs.imencode(".png", frame, buffer);// encode the frame in the buffer, according to the PNG format
		return new Image(new ByteArrayInputStream(buffer.toArray()));// build and return an Image created from the image encoded in the buffer
	}
}