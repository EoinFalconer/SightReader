package openCVImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

public class Part {
	
	ArrayList<Stave> stavesInPart = new ArrayList<Stave>();
	private String partName;
	private String partId;
	private int midiChannel;
	
	public Part(String partName, String partId, int midiChannel){
		this.partName = partName;
		this.partId = partId;
		this.midiChannel = midiChannel;
	}
	public String buildPartListXML(){
		String xml = "<score-part id=\"" + partId + "\">\n";
		xml = xml + "<part-name>" + partName + "</part-name>\n";
		xml = xml + "<part-abbreviation>"+ partName.substring(0, 1) +".</part-abbreviation>\n";
		xml = xml + "<score-instrument id=\"" + partId + "-I1\">\n";
		xml = xml + "<instrument-name>Test</instrument-name>\n";
		xml = xml + "</score-instrument>\n";
		xml = xml + "<midi-device id=\""+ partId +"-I1\" port=\"1\"></midi-device>\n";
		xml = xml + "<midi-instrument id=\""+partId+"-I1\">\n";
		xml = xml + "<midi-channel>" + midiChannel + "</midi-channel>\n";
		xml = xml + "<midi-program>53</midi-program>\n";
		xml = xml + "<volume>78.7402</volume>\n";
		xml = xml + "<pan>0</pan>\n";
		xml = xml + "</midi-instrument>\n";
		xml = xml + "</score-part>\n";
		System.out.println("xml: " + xml);
		return xml;
	}
	public String buildBarsXML(){
		String xml = "";
		int barCounter = 1;
		
		for(int i=0;i<this.stavesInPart.size();i++){
			Stave currentStave = stavesInPart.get(i);
			for(int j=0;j<currentStave.bars.size();j++){
				
				Bar currentBar = currentStave.bars.get(j);
				if(currentBar.symbols.size() > 1){
				int barLength = currentBar.endingXPixel - currentBar.beginningXPixel;
				xml = xml + "\t<measure number=\""+barCounter+"\" width=\""+barLength+"\">\n";
				if(barCounter == 1){
					xml = xml + "\t<print>\n";
					xml = xml + "\t\t<system-layout>\n";
					xml = xml + "\t\t\t<system-margins>\n";
					xml = xml + "\t\t\t\t<left-margin>109.66</left-margin>\n";
					xml = xml + "\t\t\t\t<right-margin>-0.00</right-margin>\n";
					xml = xml + "\t\t\t</system-margins>\n";
					xml = xml + "\t\t\t<top-system-distance>170.00</top-system-distance>\n";
					xml = xml + "\t\t</system-layout>\n";
					xml = xml + "\t</print>\n";
					xml = xml + "\t<attributes>\n";
					xml = xml + "\t\t<divisions>1</divisions>\n";
					xml = xml + "\t\t<key>\n";
					xml = xml + "\t\t\t<fifths>-1</fifths>\n";
					xml = xml + "\t\t</key>\n";
					xml = xml + "\t\t<time>\n";
					xml = xml + "\t\t\t<beats>4</beats>\n";
					xml = xml + "\t\t\t<beat-type>4</beat-type>\n";
					xml = xml + "\t\t</time>\n";
					xml = xml + "\t\t<clef>\n";
					if(currentStave.clef == "bass"){
						xml = xml + "\t\t\t<sign>F</sign>";
						xml = xml + "\t\t\t<line>4</line>";
					}else{
						xml = xml + "\t\t\t<sign>G</sign>\n";
						xml = xml + "\t\t\t<line>2</line>\n";
					}
					xml = xml + "\t\t</clef>\n";
					xml = xml + "\t</attributes>\n";
					xml = xml + "<direction placement=\"above\">\n";
					xml = xml + "<direction-type>\n";
					xml = xml + "<words default-y=\"40\" font-family=\"Times New Roman\" font-size=\"12\" font-weight=\"bold\">Moderato  </words>\n";
					xml = xml + "</direction-type>\n";
					xml = xml + "<direction-type>\n";
					xml = xml + "<metronome default-y=\"40\" parentheses=\"yes\">\n";
					xml = xml + "<beat-unit>quarter</beat-unit>\n";
					xml = xml + "<per-minute>c. 80</per-minute>\n";
					xml = xml + "</metronome>\n";
					xml = xml + "</direction-type>\n";
					xml = xml + "<sound tempo=\"80\"/>\n";
					xml = xml + "</direction>\n";
					
				}
				boolean currentSharp = false;
				boolean currentNatural = false;
				for(int k=0;k<currentBar.symbols.size();k++){
					
					Symbol currentSymbol = currentBar.symbols.get(k);
					if(currentSymbol.noteValues != "" && !currentSymbol.noteValues.contains("-")){
						xml = xml + "<note default-x=\""+currentSymbol.startX+"\" default-y=\""+-25.00+"\">\n";
						xml = xml + "<pitch>\n";
						xml = xml + "<step>" + currentSymbol.noteValues + "</step>\n";
						if(currentSymbol.noteValues.equals("B") && !currentNatural){
							xml = xml + "<alter>-1</alter>";
						}else if(currentSharp){
							xml = xml + "<alter>1</alter>";
							currentSharp = false;
						}else if(currentNatural){
							currentNatural = false;
						}
						xml = xml + "<octave>"+currentSymbol.octaves+"</octave>\n";
						xml = xml + "</pitch>\n";
						
						switch(currentSymbol.rhythmNames){
							case "quarter": xml = xml + "<duration>1</duration>\n";
											break;
							case "half": xml = xml + "<duration>2</duration>\n";
										break;
							default: xml = xml + "<duration>1</duration>\n";
						}
						
						xml = xml + "<voice>1</voice>\n";
						if(currentSymbol.rhythmNames != ""){
							xml = xml + "<type>"+currentSymbol.rhythmNames+"</type>\n";
						}else{
							xml = xml + "<type>quarter</type>\n";
						}
						if(k < currentBar.symbols.size()-1){
							if(currentBar.symbols.get(k+1).isADot){
								xml = xml + "<dot/>\n";
							}
						}
						if(Integer.parseInt(currentSymbol.noteHeadYValues) > currentStave.midPoint){
							xml = xml + "<stem>up</stem>\n";
						}else{
							xml = xml + "<stem>down</stem>\n";
						}
						if(currentSymbol.accidental.equals("sharp")){
							currentSharp = true;
							System.out.println("setting sharp true");
						}else if(currentSymbol.accidental.equals("natural")){
							currentNatural = true;
							System.out.println("setting natural true");
						}
						xml = xml + "</note>\n";
					
					}else if(currentSymbol.noteValues.contains("-")){
						String[] noteRhythms = currentSymbol.rhythmNames.split("-");
						String[] noteValues = currentSymbol.noteValues.split("-");
						String[] noteYValues = currentSymbol.noteHeadYValues.split("-");
						String[] octaves = currentSymbol.octaves.split("-");
						
						for(int l=0;l<noteRhythms.length;l++){
							System.out.println("noteRhytms: " + noteRhythms[l]);
							System.out.println("noteValues: " + noteValues[l]);
							System.out.println("noteYValues: " + noteYValues[l]);
							xml = xml + "<note default-x=\""+currentSymbol.startX+"\" default-y=\""+-25.00+"\">\n";
							xml = xml + "<pitch>\n";
							xml = xml + "<step>" + noteValues[l] + "</step>\n";
							if(currentSymbol.noteValues.equals("B") && !currentNatural){
								xml = xml + "<alter>-1</alter>";
							}else if(currentSharp){
								xml = xml + "<alter>1</alter>";
							}
							xml = xml + "<octave>"+ octaves[l] +"</octave>\n";
							xml = xml + "</pitch>\n";
							xml = xml + "<duration>1</duration>\n";
							xml = xml + "<voice>1</voice>\n";
							xml = xml + "<type>"+noteRhythms[l]+"</type>\n";
							
							if(Integer.parseInt(noteYValues[l]) > currentStave.midPoint){
								xml = xml + "<stem>up</stem>\n";
							}else{
								xml = xml + "<stem>down</stem>\n";
							}
							if(l == 0){
								xml = xml + "<beam number=\"1\">begin</beam>\n";
							}else if(l == noteRhythms.length-1){
								xml = xml + "<beam number=\"1\">end</beam>\n";
							}else{
								xml = xml + "<beam number=\"1\">continue</beam>\n";
							}
							xml = xml + "</note>\n";
							
						}
					}else{ 
						
						//adding in rests
					}
					if(currentSymbol.accidental.equals("sharp")){
						currentSharp = true;
						System.out.println("setting sharp true");
					}else if(currentSymbol.accidental.equals("natural")){
						currentNatural = true;
						System.out.println("setting natural true");
					}
				}
				}
				if(j == currentStave.bars.size()-1){
					xml = xml + "<barline location=\"right\">\n";
					xml = xml + "<bar-style>light-heavy</bar-style>\n";
					xml = xml + "</barline>\n";
				}
				xml = xml + "</measure>\n";
				barCounter++;
			}
		}
		return xml;
	}
	public void buildPartXMLFile(String text, String scoreName){
		String xml = "";
		xml = xml + "<part-list>";
		xml = this.buildPartListXML();
		xml = xml + "</part-list>";
		xml = xml + "<part id=\""+this.partId+"\">";
		xml = xml + this.buildBarsXML();
		xml = xml + "</part>";
		xml = xml + "</score-partwise>";
		System.out.println(scoreName);
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	               new FileOutputStream(scoreName+"CONVERTED"+this.partId+".xml"), "utf-8"))) {
	    writer.write(text+xml);
	 } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
}
