
public class ReedMuller {

    private int[][]    genMatrix    = null;
    private int[]      genIntMatrix = null;
    private int[][]    RM_Table     = null;
    private int        r            = 0;
    private int[]	   Y_IntTable 	= null;
	private int[] 	   X_IntTable 	= null;
	private int[][]	   X_Table 		= null;

    public ReedMuller(int r) {
        this.r = r;
    }

    /**
     * Function which inverts bits with certain probability passed as parameter.
     * This function seeks an image without applying the coding to the bits of
     * each pixel of the image.
     * 
     * @param image
     *            The image to be noised.
     * @param prob
     *            The probability for each bit representing the pixel shade to
     *            be inverted.
     * @return The noised image.
     */
    public Pgm noise(Pgm img, int prob, boolean verbose) {
    	Pgm image = new Pgm();
    	image = img;
    	
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int pixel = (image.getPixels())[i][j];
                String bpixel = toBits(pixel);
                char[] cbpixel = bpixel.toCharArray();

                if (verbose) {
                    System.out.print(cbpixel);
                    System.out.println(" <");
                }
               
                for (int k = 0; k < Integer.SIZE-1; k++) {
                    if (((float) (Math.random()) * 100) < prob) {
                        pixel = pixel ^ (1 << k);
                    }
                }

                if (verbose) {
                    System.out.println(pixel);
                }

                if (verbose) {
                    System.out.println(pixel);
                }
                image.setPixel(i, j, pixel);
            }
        }
        return image;
    }

    /**
     * Function which denoises image.
     * 
     * @param image
     *            which will be denoised
     * @return image without noise
     */   
    public Pgm denoiseHAM(Pgm img, boolean verbose) {

    	Pgm image = new Pgm();
    	image = img;

		buildGenMatrix(this.r);
		buildRMTable();

		int[] dist_Table = null;
		int[][] pixels = image.getPixels();
		
		System.out.println("Calculate Hamming distance!");
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
			
				int pixel = pixels[i][j];
				dist_Table = new int[(int) Math.pow(2, this.r + 1)];
				
				for (int k = 0; k < (int) Math.pow(2, this.r + 1); k++) {
					int y = this.Y_IntTable[k]; 					
					dist_Table[k] = hamming(pixel, y);
				}
				
				int position = 0;
				position = getPositionOfRM(dist_Table);
				image.setPixel(i, j, Y_IntTable[position]);
			}
		}
		
		if(verbose){
			for (int k = 0; k < (int) Math.pow(2, this.r + 1); k++) {
				System.out.println(dist_Table[k]);
			}
		}
		
		return image;
	}

    /**
     * Function which denoises image using DFT.
     * 
     * @param image
     *            which will be denoised
     * @return image without noise
     */
    public Pgm denoiseDFT(Pgm img, boolean verbose) {
    	Pgm image = new Pgm();
    	image = img;
        
    	buildGenMatrix(this.r);
		buildRMTable();

		int[] DFT_dist = null;
        int number_of_elements = (int)Math.pow(2, this.r);
		int[][] pixels = image.getPixels();
            
        //Denoising by Discrete Fourier Transform.            
        System.out.println("Calculate DFT!");
        for (int i = 0; i < image.getWidth(); i++) {
        	for (int j = 0; j < image.getHeight(); j++) {
        		
        		int pixel = pixels[i][j];
        		int dist = 0;
        		DFT_dist = new int [(int)Math.pow(2, this.r+1)];
        		
        		for (int k = 0; k < (int)Math.pow(2, this.r); k++) {
					int y = this.Y_IntTable[k]; 										
					//Compute distance between pixel from image and y from RM code.
					dist = hamming(pixel, y);
					//Assign to the table of distances.
					DFT_dist[k] = dist;
					//Calculate simply for the second half of RM code.
					DFT_dist[(int)Math.pow(2, this.r) + k] = number_of_elements - 2*dist; 
        		}	
       
        		//Get the position of the closest element in RM code.
        		int position = 0; 
        		position = getPositionOfRM(DFT_dist);
        		//Replace shade in image.
        		image.setPixel(i,j,Y_IntTable[position]);
            }
        }
            
        if(verbose){
        	for(int k=0; k < (int)Math.pow(2, r+1); k++) {
        		System.out.println(DFT_dist[k]);
        	}   	
        }
        
        return image;
    }

    /**
     * Function which denoises image using FFT.
     * 
     * @param image
     *            which will be denoised
     * @return image without noise
     */
    public Pgm denoiseFFT(Pgm img, boolean verbose) {
    	Pgm image = new Pgm();
    	image = img;
            
    	buildGenMatrix(this.r);
		buildRMTable();

		int[][] pixels = image.getPixels();
            
		//Using FFT for denosing.
        int[][] H_matrix = null;
        int[] FFT_table = null;
            
        System.out.println("Calculate FFT!");
        for (int i = 0; i < image.getWidth(); i++) {
        	for (int j = 0; j < image.getHeight(); j++) {
        		int pixel = pixels[i][j];
        		//String binPixel = Integer.toBinaryString(pixel);//intToBinary(pixel, 31);
                //System.out.println(pixel + " " + binPixel);
        		
        		String binPixel = Integer.toString(pixel, 2);
        		if(pixel < 0){
        			binPixel = binPixel.replaceFirst("-", "1");
        		}
        		
        	
        		/*
        		String binPixel = new String();
        		binPixel = "00000000000000000000000000000000";
        		
        		for(int g=binPixel.length()-1, t=0; g>=0 | t<(int)Math.pow(2, this.r); g--, t++){
        			int a = (pixel & t);
        			if(a == 1){
        				char[] tab = new char[32];
        				tab = binPixel.toCharArray();
        				tab[g] = '1';
        				binPixel = String.copyValueOf(tab);
        				//System.out.println(binPixel);
        			}
        		}
        		*/
        		//System.out.println(binPixel + " " + binPixel.length());
        		
                H_matrix = new int[(int)Math.pow(2, this.r)][(int)Math.pow(2, this.r)];
                FFT_table = new int[(int)Math.pow(2, this.r+1)];
                
                for(int b=0; b<(int)Math.pow(2, this.r); b++){
                	for(int z=0; z<(int)Math.pow(2, this.r); z++){
                		H_matrix[b][z] = (int)Math.pow(-1, RM_Table[b][z]);
                	}
                }
                
                /*for(int k=0; k<(int)Math.pow(2, this.r); k++){
                	int u = 0;
                	int dist = 0;
                	int dist1 = 0;
                	dist = hamming(pixel, this.Y_IntTable[k]);
                	//System.out.println(binPixel.length() + " " + RM_Table[k].length);
                    
                	for(int l=0; l<(int)Math.pow(2, this.r); l++){
                    	//u = u + (int)Math.pow(-1, RM_Table[k][l]) * (int)Math.pow(-1, Integer.valueOf((String.valueOf(binPixel.charAt(l)))));
                		if(l<binPixel.length())
                    	u = (int)Math.pow(-1, Integer.valueOf((String.valueOf(binPixel.charAt(l)))));
                    	
                		//H_matrix[k][l] = (int)Math.pow(-1, RM_Table[k][l]);
                		
                    	dist1 = dist1 + u * H_matrix[k][l];
                    }
                	
                    FFT_table[k] = dist;
                    FFT_table[(int)Math.pow(2, this.r) + k] = (int)Math.pow(2, this.r) - 2*dist1;
                }
              	*/
                
                for(int k=0; k<(int)Math.pow(2, this.r); k++){
                	int u = 0;
                	int dist1 = 0;
                	int dist = 0;
                	dist = hamming(pixel, this.Y_IntTable[k]);
                	
                	for(int l=0; l<(int)Math.pow(2, this.r); l++){
                		if(l<binPixel.length())
                    	u = (int)Math.pow(-1, Integer.valueOf((String.valueOf(binPixel.charAt(l)))));
                		dist1 = dist1 + u * H_matrix[l][k];
                    }
                	
                	FFT_table[k] = dist;
                    FFT_table[(int)Math.pow(2, this.r) + k] = (int)Math.pow(2, this.r) - 2*dist1;
                }
                
                //Get the position of the closest element in RM code.
        		int position = 0;
        		position = getPositionOfRM(FFT_table);
        		//Replace shade in image.
        		image.setPixel(i,j,Y_IntTable[position]);
            }
         }   
        
        if(verbose){
        	System.out.println("H" + r + " matrix !");
            for(int k=0; k<(int)Math.pow(2, this.r); k++){
                for(int l=0; l<(int)Math.pow(2, this.r); l++){
                	System.out.print(H_matrix[k][l] + " ");
                }
                System.out.println();
            }
        }
         
         return image;
    }

    /**
     * Function which finds position in Reed Muller table based on Hamming distance.
     * @param dist_Tab - table with all distance between pixel from image and Reed Muller code.
     * @return counter - position of proper value in Reed Muller code.
     */
    public int getPositionOfRM(int[] dist_Tab) {
		int min = dist_Tab[0];
		int counter = 0;
		for (int a = 0; a < dist_Tab.length; a++) {
			if (dist_Tab[a] < min) {
				min = dist_Tab[a];
				counter = a;
			}
		}
		return counter;
	}

    public static String intToBinary(int num, int r) {
        int copy = num;
        String sb = "";
        for (int i = r; i >= 0; i--) {
            sb = (copy & 1) + sb;
            copy = copy >>>= 1;
        }
        String jj = new StringBuffer(sb).reverse().toString();
        return jj;
    }

    public void buildGenMatrix(int r) {
        // the array way
        this.genMatrix = new int[r + 1][(int) Math.pow(2, r)];
        int[] bj;

        // Generator's matrix
        for (int j = 0; j < Math.pow(2, r); j++) {
            String jj = new StringBuffer(toBits(j)).reverse().toString();
            bj = new int[jj.length()];
            for (int i = jj.length() - 1; i >= 0; i--) {
                bj[i] = Integer.valueOf((String.valueOf(jj.charAt(i))));
                this.genMatrix[i][j] = bj[i];
            }
        }
        for (int j = 0; j < Math.pow(2, r); j++) {
            this.genMatrix[r][j] = 1;
        }

        // The full int way
        int R = (int) Math.pow(2, r + 1);
        genIntMatrix = new int[r + 1];

        for (int j = 0; j < r; j++) {
            int k = R - (R - 2 * (int) Math.pow(2, j - 1));
            for (int l = 0; l < ((k < 1 ? R : R / k) / 2); l++) {
                for (int m = 0; m < (k == 0 ? 1 : k); m++) {
                    genIntMatrix[j] <<= 1;
                }
                for (int m = 0; m < (k == 0 ? 1 : k); m++) {
                    int z = genIntMatrix[j] ^ 1;
                    z <<= 1;
                    genIntMatrix[j] = z;
                }
            }
        }
        int zz = 0;
        if (R == 64) { // 32bits long encoded word.
            genIntMatrix[r] = -1;
        } else {
            for (int i = 0; i < R; i++) {
                zz += 1;
                zz <<= 1;
                genIntMatrix[r] = zz;
            }
        }
        for (int i = 0; i < r + 1; i++) {
            genIntMatrix[i] = Integer.rotateRight(genIntMatrix[i], 1);
        }
    }
    
    public void buildRMTable() {
		System.out.println("Reed-Muller table!");

		// Table of Reed-Muller code.
		this.RM_Table = new int[(int) Math.pow(2, this.r + 1)][(int) Math.pow(2, this.r)];

		// Table of messages x of length 6.
		this.X_Table = new int[(int) Math.pow(2, this.r + 1)][this.r + 1];
		this.X_IntTable = new int[(int) Math.pow(2, this.r + 1)];
		this.Y_IntTable = new int[(int) Math.pow(2, this.r + 1)];

		System.out.println("Table of messages X as binary and integer!");

		// Generate table of messages x.
		for (int i = 0; i < (int) Math.pow(2, this.r + 1); i++) {
			String val = intToBinary(i);
			
			for (int j = 0; j < this.r + 1; j++) {
				this.X_Table[i][j] = Integer.parseInt(String.valueOf(val
						.charAt(j)));
				System.out.print(this.X_Table[i][j]);
			}
			System.out.println();
			this.X_IntTable[i] = Integer.valueOf(val, 2);
			System.out.println(this.X_IntTable[i]);
		}

		System.out.println("TABLE OF Y MESSAGES !!!");

		for (int i = 0; i < (int) Math.pow(2, r + 1); i++) {
			for (int j = 0; j < (int) Math.pow(2, r); j++) {
				int y = 0;
				for (int k = 0; k < r + 1; k++) {
					int base = this.genMatrix[k][j];
					int x = Integer.valueOf(this.X_Table[i][k]);

					y = y ^ (x & base);
					this.RM_Table[i][j] = y;

				}

				System.out.print(RM_Table[i][j] + " ");
			}
			System.out.println();
		}
		
		for (int i = 0; i < (int) Math.pow(2, this.r + 1); i++) {
			String val = "";
			for (int j = 0; j < (int) Math.pow(2, this.r); j++) {
				val = val + Integer.toString(RM_Table[i][j]);
			}

			int a = 0;
			for (int k=0; k<val.length(); k++){
	                a <<= 1;
	                if (val.charAt(k) == '1'){
	                    a += 0x1;
	                }
	        }

			this.Y_IntTable[i] = a;
		}
		
		System.out.println("TABLE OF Y MESSAGES as integer!!!");
		for (int a = 0; a < (int) Math.pow(2, this.r + 1); a++) {
			System.out.println(this.Y_IntTable[a]);
		}

	}

    /**
	 * Converts integer to binary string.
	 * 
	 * @param num
	 * @return
	 */
	public String intToBinary(int num) {
		int copy = num;
		String sb = "";
		for (int i = this.r; i >= 0; i--) {
			sb = sb + (copy & 1);
			copy = copy >>>= 1;
		}
		return sb;
	}
	
	public int inBin(String g) {
		int zzz = 0;
		for (int f = g.length()-1; f >= 0 ; f--) {
			zzz += (int) Math.pow(2, f+1) * Integer.parseInt(g.charAt(f) + "");
		}
		System.out.println("Bin: " + g);
		System.out.println("Bin: " + Integer.toBinaryString(zzz));
		System.out.println("Inbin: " + zzz);
		return zzz;
	}
    
    /**
     * Prints out the generators matrix.
     */
    public void showGenMatrix() {
        if (genMatrix != null) {
            for (int i = 0; i < genMatrix.length; i++) {
                for (int j = 0; j < genMatrix[i].length; j++) {
                    System.out.print(genMatrix[i][j]);
                }
                System.out.print("\n");
            }
            System.out.print("\n");
        } else {
            System.out.println("No generators matix\n");
        }
    }

    public void showGenIntMatrix() {
        if (genIntMatrix != null) {
            for (int i = 0; i < r + 1; i++) {
                System.out.println(this.genIntMatrix[i]);
                System.out.println(Integer.toBinaryString(genIntMatrix[i]));
            }
        } else {
            System.out.println("No generators matix\n");
        }
    }

    /**
     * Function that encodes the image.
     * 
     * @param image
     *            The images that will be encoded
     * @param r
     * @param verbose
     * @return image The encoded image.
     */
    public Pgm encode(Pgm img, Boolean verbose) {
    	
    	Pgm image = new Pgm();
    	image = img;
        int[][] pixels = image.getPixels();
        int CodedPixel;
        
        buildGenMatrix(this.r);
        buildRMTable();

        if (verbose) {
            showGenIntMatrix();
        }

        // Coding part

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                CodedPixel = 0x00000000; //

                int pixel = pixels[i][j];
                String bpixel = Integer.toBinaryString(pixel);
                if (verbose) {
                    System.out.println("pixel: " + pixel + "\nbpixel: "
                            + bpixel);
                    System.out.println(bpixel.length());
                }
                for (int l = 0, m = bpixel.length() - 1; l < bpixel.length()
                        | m >= 0; l++, m--) {
                    int x = bpixel.charAt(l) - 48;
                    if (Integer.valueOf(x) == 1) {
                        if (verbose) {
                            System.out
                                    .format("%32s\n",
                                            Integer.toBinaryString(genIntMatrix[genIntMatrix.length
                                                    - 1 - m]));
                        }
                        CodedPixel = ((Integer) CodedPixel)
                                ^ ((Integer) (genIntMatrix[genIntMatrix.length
                                        - 1 - m]));
                    }
                }
                if (verbose) {
                    System.out.format("%32s\n",
                            Integer.toBinaryString(CodedPixel));
                }
                pixels[i][j] = CodedPixel;
            }
        }
        image.setPixels(pixels);
        return image;
    }

    /**
     * Function which decodes image.
     * 
     * @param image
     *            which will be decoded
     * @return image which is decoded
     */
    public Pgm decode(Pgm img, boolean verbose) {

    	Pgm image = new Pgm();
    	image = img;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int pixel = image.getPixel(i, j);

                boolean negative = false;
                for (int a = 0; a < (int) Math.pow(2, this.r + 1); a++) {
                      
                	int zzz = Y_IntTable[a];

                    if (pixel < 0) {
                        pixel = pixel ^ 0xffffffff;
                        negative = true;
                    }
                    if (zzz == pixel) {
                        int yyy = X_IntTable[a];
                        if (negative == true) {
                            yyy += 1;
                            image.setPixel(i, j, yyy);
                            negative = false;
                        } else {
                            image.setPixel(i, j, yyy);
                            negative = false;
                        }
                        break;
                    }
                }
            }
        }
        return image;
    }

    private String toBits(int number) {
        String binNumber = "";
        if (number == 0) {
            return "0";
        }
        while (number != 0) {
            binNumber = (number % 2) + binNumber;
            number /= 2;
        }
       
        return binNumber;
    }

    public int toNumber(long bits) {
        double number = 0;

        String binNumber = Long.toString(bits);
        for (int i = 0; i < binNumber.length(); i++) {
            if (binNumber.charAt(i) == '1') {
                number = number + Math.pow(2, binNumber.length() - 1 - i);
            }
        }
        return (int) number;
    }

    public int hamming(Integer a, Integer b) {
		int distance = -1;
		if (a == null || b == null) {
			return distance;
		} else {
			distance = 0;
			Integer diff = (a ^ b);
			for (int i = 0; i < Integer.SIZE; i++) {
				if ((diff & 1) == 1) {
					distance++;
				}
				diff >>= 1;
			}
			return distance;
		}
	}
}
