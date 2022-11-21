package com.gmail.berndivader.mythicmobsext.mechanics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;
import com.gmail.berndivader.mythicmobsext.jboolexpr.MathInterpreter;
import com.gmail.berndivader.mythicmobsext.utils.RandomDouble;

@ExternalAnnotation(name="particleimage", author="Seyarada")
public class ParticleImage extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

// TODO:
// Optimize this

// Change GIF start so it asks per frame in the loop?
// Getting all the frames at once then displaying it seems to cause
// a notable delay before the particles start for big files
	
	String fileImage;
	String backgroundColor;
	String resize;
	String transform;
	int loop;
	int skip;
	double scale;
	Long interval;
	PlaceholderString scaleAmount;
	Particle particle;
	
	public ParticleImage(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		fileImage = mlc.getString(new String[] {"file", "f"}, null);
		scaleAmount = PlaceholderString.of(mlc.getString(new String[] {"scale", "s"}, "4"));
		backgroundColor = mlc.getString(new String[] {"bgcolor", "color", "c"}, "white");
		resize = mlc.getString("resize", "false");
		interval = mlc.getLong(new String[]{"interval"}, 5);
		skip = mlc.getInteger("skip", 5);
		transform = mlc.getString("transform", "x,0,y");
		particle = Particle.valueOf(mlc.getString(new String[] {"particle", "p"}, "REDSTONE").toUpperCase());
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity p) {
		scale = new RandomDouble(scaleAmount.get(data,p)).rollDouble();
		Location loc = BukkitAdapter.adapt(p.getLocation());
		start(loc);
		return SkillResult.SUCCESS;
	}
	
	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation aL) {
		AbstractEntity caster = data.getTrigger();
		scale = new RandomDouble(scaleAmount.get(data,caster)).rollDouble();
		Location loc = BukkitAdapter.adapt(aL);
		start(loc);
		return SkillResult.SUCCESS;
	}

	public void start(Location loc) {
		
		if(fileImage.contains(".gif")) {
			// GIF animation
			File dir = new File(Main.getPlugin().getDataFolder().getPath() + "/images/" + fileImage);
			try {
				ArrayList<BufferedImage> frames = getFrames(dir);
				loop = 0;
				int size = frames.size();
				new BukkitRunnable() {
					@Override
					public void run() {
						if (loop < size) {
							draw(frames.get(loop), loc);
							loop ++;
						}
						else {
							this.cancel();
						}
					}
				}.runTaskTimerAsynchronously(Main.getPlugin(), 1L, interval);
				
			} catch (IOException e) {e.printStackTrace();}
			
		}
		else {
			// Normal picture
			BufferedImage img = null;
			File dir = new File(Main.getPlugin().getDataFolder().getPath() + "/images/" + fileImage);
			try {
				img = ImageIO.read(dir);
			} catch (IOException e) {e.printStackTrace();}
			
			if(img != null) draw(img, loc);
		}
	}
	
	public void draw(BufferedImage img, Location loc) {
		if(!resize.equals("false")) {
			
			String[] x = resize.split(",");
			int tWidth = Integer.parseInt(x[0]);
			int tHeight = Integer.parseInt(x[1]);
			
			try {
				img = resizeImage(img, tWidth, tHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		double width = img.getWidth()-1;
		double height = img.getHeight()-1;
		@Nullable
		World world = loc.getWorld();
	    
	    int k = 1;
	    for (int i = 0; i < width; i++) {
	    	for (int o = 0; o < height; o++) {
	    		  
	    		  if (k >= skip) {
	    			  k = 1;
		    		  
			    	  int pixel = img.getRGB(i, o);
			    	  int r = (pixel>>16) & 0xff;
			    	  int g = (pixel>>8) & 0xff;
			    	  int b = pixel & 0xff;

			    	  int bgRed, bgGreen, bgBlue;
			    	  bgRed = bgGreen = bgBlue = 255;
			    	  if (!backgroundColor.equals("white") && !backgroundColor.contains(",")) {
						  bgRed = 0;
						  bgGreen = 0;
						  bgBlue = 0;
			    	  }
			    	  else if (backgroundColor.contains(",")) {
			    		  String[] colors = backgroundColor.split(",");
						  bgRed = Integer.parseInt(colors[0]);
						  bgGreen = Integer.parseInt(colors[1]);
						  bgBlue = Integer.parseInt(colors[2]);
			    	  }
	
			    	  // Removes white/black pixels to remove the background
			    	  if ( r != bgRed && g != bgGreen && b != bgBlue )
			    	  {
			    		  double x = (i/scale) - ((width/scale)/2.0);
			    		  double y = (o/scale) - ((height/scale)/2.0);
			    		  
			    		  String t1 = transform.split(",")[0];
			    		  String t2 = transform.split(",")[1];
			    		  String t3 = transform.split(",")[2];
			    		  t1 = t1.replace("x", String.valueOf(x)).replace("y", String.valueOf(y));
			    		  t2 = t2.replace("x", String.valueOf(x)).replace("y", String.valueOf(y));
			    		  t3 = t3.replace("x", String.valueOf(x)).replace("y", String.valueOf(y));
			    		  double result1 = MathInterpreter.parse(t1, new HashMap<>()).eval();
			    		  double result2 = MathInterpreter.parse(t2, new HashMap<>()).eval();
			    		  double result3 = MathInterpreter.parse(t3, new HashMap<>()).eval();
			    		  
			    		  
			    		  Location finalLoc = loc.clone();
			    		  finalLoc.add(result1, result2, result3);
			    		  if(particle==Particle.valueOf("REDSTONE")) {
			    			  Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), 1);
				    	      world.spawnParticle(Particle.REDSTONE, finalLoc, 1, 0, 0, 0, 0, dustOptions);
			    		  } else {
			    			  world.spawnParticle(particle, finalLoc, 1, 0, 0, 0, 0);
			    		  }
			    	  }
			    		  
			    	} else { k ++; }
	    		}
	    	}
	}
	
	
	public ArrayList<BufferedImage> getFrames(File gif) throws IOException {
		ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
		try {
		    String[] imageatt = new String[]{
		            "imageLeftPosition",
		            "imageTopPosition",
		            "imageWidth",
		            "imageHeight"
		    };    

		    ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
		    ImageInputStream ciis = ImageIO.createImageInputStream(gif);
		    reader.setInput(ciis, false);

		    int noi = reader.getNumImages(true);
		    BufferedImage master = null;

		    for (int i = 0; i < noi; i++) {
		        BufferedImage image = reader.read(i);
		        IIOMetadata metadata = reader.getImageMetadata(i);
		        
		        Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
		        NodeList children = tree.getChildNodes();

		        for (int j = 0; j < children.getLength(); j++) {
		            Node nodeItem = children.item(j);

		            if(nodeItem.getNodeName().equals("ImageDescriptor")){
		                Map<String, Integer> imageAttr = new HashMap<>();

		                for (int k = 0; k < imageatt.length; k++) {
		                    NamedNodeMap attr = nodeItem.getAttributes();
		                    Node attnode = attr.getNamedItem(imageatt[k]);
		                    imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
		                }
		                if(i==0){
		                    master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
		                }
		                master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"), imageAttr.get("imageTopPosition"), null);
		            }
		        }
		        
				// TODO: Change this part
		        File dir = new File( Main.getPlugin().getDataFolder().getPath() + "/images/qlJ06jLEg8.png");
		        ImageIO.write(master, "GIF", dir);
		        BufferedImage a = ImageIO.read(dir);
		        frames.add(a);
		        dir.delete();
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return frames;
	}
	
	BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
	    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
	    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
	    return outputImage;
	}

}
