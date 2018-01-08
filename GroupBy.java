import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


// input format: x y AggregationAttribute
public class GroupBy extends Configured implements Tool
{
	
	public static class MyMapper extends Mapper<LongWritable, Text, VectorElem, Text>
	{
				
		public void map(LongWritable inputKey, Text inputValue, Context context) throws IOException, InterruptedException
 		{
			Configuration conf = context.getConfiguration();
			Text aggAttr = new Text(inputValue.toString().split(",")[0]);
			int numDim = Integer.parseInt(conf.get("numDim"));
 			VectorElem vector = new VectorElem(inputValue.toString(),numDim);
 			System.out.println("vector mapped: " + vector);
 			context.write(vector, aggAttr);
 		}//end map
		
	}//end of myMapper
	
	public static class MyReducer extends Reducer<VectorElem, Text, Text, Text>
	{
		
		@Override
		protected void reduce(VectorElem key, Iterable<Text> values, Context context) throws IOException,
				InterruptedException 
		{			
			// Get user selection from context
			String aggStr;
			aggStr = context.getConfiguration().get("aggChoice", "");
			if("".equals(aggStr))
			{
				System.out.println("Error: Unable to get 'aggChoice' from configuration");
				System.out.println(aggStr);
			}
			int aggInt = Integer.parseInt(aggStr);
			
			// Run selected aggregation function
			if(aggInt == 1)
			{
				// _Function: Average value
	 			int count = 0;
				double sum = 0.0;
				double temp;
				String tempStr;
				for (Text value : values) // Average aggregation routine
				{
					tempStr = value.toString();
					temp = Double.parseDouble(tempStr);
					count++;
					sum = sum + temp;				
				}
				double avg = sum / count;
				
				context.write(new Text(key.toString()), new Text(Double.toString(avg)));
			}
			else if(aggInt == 2)
			{
				// _Function: Count
				int count = 0;
				for (Text value : values)
				{
					count++;			
				}
				
				context.write(new Text(key.toString()), new Text(Double.toString(count)));
			}
			else if(aggInt == 3)
			{
				// _Function: Minimum Value
				int countMin = 0;
				double minCurrent = Double.MAX_VALUE;
				double currMinDbl = 0.0;
				String currMinStr;

				for (Text value : values)
					{
						currMinStr = value.toString();
						currMinDbl = Double.parseDouble(currMinStr);
						minCurrent = Math.min(currMinDbl, minCurrent);
			
						countMin++;	// For debugging
					}
					
				context.write(new Text(key.toString()), new Text(Double.toString(minCurrent)));
			}
			else if(aggInt == 4)
			{
				// _Function: Maximum Value
				int countMin = 0;
				double maxCurrent = Double.MIN_VALUE;
				double currMaxDbl = 0.0;
				String currMaxStr;

				for (Text value : values)
					{
						currMaxStr = value.toString();
						currMaxDbl = Double.parseDouble(currMaxStr);
						maxCurrent = Math.max(currMaxDbl, maxCurrent);
			
						countMin++;	// For debugging
					}

				context.write(new Text(key.toString()), new Text(Double.toString(maxCurrent)));
			}
			else if(aggInt == 5)
			{
				// _Function: Sum
				int count = 0;
				double sum = 0.0;
				double temp;
				String tempStr;
				for (Text value : values)
				{
					tempStr = value.toString();
					temp = Double.parseDouble(tempStr);
					count++;
					sum = sum + temp;				
				}
				
				context.write(new Text(key.toString()), new Text(Double.toString(sum)));
			}
			
		}//end reduce
	}//end MyReducer
	
	public static class VectorPartitioner extends Partitioner<VectorElem, Text>
	{
		@Override
		public int getPartition(VectorElem key, Text value, int numP)
		{                        
                        double sumOne = 0.0; // Sum for even numbered elements
                        double sumTwo = 0.0; // Sum for odd numbered elements
                        double primeOne = 157.0;
                        double primeTwo = 127.0;
                        int elemCnt = 0;
                        for (double elem : key.elements) {
                            if (elemCnt % 2 == 0) {
                                sumOne += elem * primeOne;
                            } else if (elemCnt % 2 != 0) {
                                sumTwo += elem * primeTwo;
                            }
                            elemCnt++;
                        }
			
			// Add the key elements and multiply by prime # to generate reproducible hash, then mod by # of reducers
			return (int)((Math.abs(sumOne) + Math.abs(sumTwo)) % numP);
			
		}//end getPartition
	}//end VectorPartitioner

	public static class VectorGrouper implements RawComparator<VectorElem>
	{
		
		@Override
		public int compare(VectorElem key1, VectorElem key2)
	    {		
            for (int index = 0; index < key1.getElemsSize(); index++) //compare vectors
            {
                if (key1.getElem(index) != key2.getElem(index)) 
                {
                    return (key1.getElem(index) < key2.getElem(index)) ? -1 : 1;
                }
            }
            return 0; // Should be for if all DIMs are equal
		}
	 
		@Override
	    public int compare(	byte[] b1, int s1, int l1, 
                                byte[] b2, int s2, int l2) 
		{			
			long x1 = WritableComparator.readLong(b1, s1);
			long y1 = WritableComparator.readLong(b1, s1 + (Long.SIZE / 8));
			long x2 = WritableComparator.readLong(b2, s2);
			long y2 = WritableComparator.readLong(b2, s2 + (Long.SIZE / 8));
			
			if (x1 != x2)
				return x1 < x2 ? -1 : 1;
			else if (y1 != y2)
				return y1 < y2 ? -1 : 1;
			else
				return 0;
	    }	
	}
	
	public static void main(String[] args) throws Exception
	{
		int res = ToolRunner.run(new Configuration(), new GroupBy(), args);
		System.exit(res);
	}
	
	public int run(String[] args) throws Exception
	{
		// Check arguments are present and valid
		// Arg choices: Avg = 1, Cnt = 2, Min = 3, Max = 4, Sum = 5
		if(args.length != 3) 
		{
			System.out.println("Usage: bin/hadoop jar GroupBy.jar GroupBy <agg method> <numReducers> <dimensionality>");
			System.out.println("args length incorrect, length: " + args.length);
			return -1;
		}
		
		int aggChoice;
        int numReduce;
        int numDim;
		
		try
		{
			aggChoice = new Integer(args[0]);
            numReduce = new Integer(args[1]);
            numDim = new Integer(args[2]);
			System.out.println("Aggregation selection: " + aggChoice);
            System.out.println("Number of reducers: " + numReduce);
            System.out.println("Number of dimensions: " + numDim);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Usage: bin/hadoop jar GroupBy.jar GroupBy <agg method> <numReducers> <dimensionality>");
			System.out.println("Aggregation choices: 1=Avg, 2=Cnt, 3=Min, 4=Max, 5=Sum");
			return -1;
		}
                
		Configuration conf = new Configuration();
		Path in = new Path("GBData.csv");
		Path out = new Path("GB_Output");
		conf.set("in.path", in.toString());
		conf.set("out.path", out.toString());
		
		// Set user selection to configuration to pass to reducer
		String aggSel = Integer.toString(aggChoice);
		conf.set("aggChoice", aggSel);
		String dimSel = Integer.toString(numDim);
		conf.set("numDim", dimSel);
		
		Job job = new Job(conf);
		job.setJobName("Group By");
		job.setNumReduceTasks(numReduce);
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		job.setJarByClass(GroupBy.class);
		
		FileInputFormat.addInputPath(job, in);
		FileSystem fs = FileSystem.get(conf);
		if (fs.exists(out)) 
		{
                    fs.delete(out, true);
		}
		FileOutputFormat.setOutputPath(job, out);
					
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapOutputKeyClass(VectorElem.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setPartitionerClass(VectorPartitioner.class);
		job.setSortComparatorClass(VectorGrouper.class);
		job.setGroupingComparatorClass(VectorGrouper.class);
	    	
		job.waitForCompletion(true);
        
		return 0;
	}
	
	public static class VectorElem implements WritableComparable<VectorElem> 
	{
        public int dim;
        
        // DATA
        ArrayList<Double> elements;
       
        // CONSTRUCTOR
        VectorElem() 
        {
            elements = new ArrayList<>(); 
        }

        VectorElem(int newDim) 
        {
            dim = newDim;
            elements = new ArrayList<>();
        }

        VectorElem(String line, int newDim) 
        {
            dim = newDim;
            elements = new ArrayList<>();
			
            for (int i = 0; i < dim; i++) 
            {	//start at i + 1 because the first number in the line is the aggregation attribute
                elements.add(Double.parseDouble(line.split(",")[i + 1]));//adds at the end of the list
            }
        }

        VectorElem(String text, boolean FurtherRound) 
        {
            if(FurtherRound)
            {
            	StringTokenizer line = new StringTokenizer(text);
            	for (int i = 0; i < dim; i++) 
            	{
                    elements.add(Double.parseDouble(line.nextToken().split(",")[i + 1]));//adds @ end of the list
            	}
            }
        }

        VectorElem(VectorElem o) 
        {
            this.dim = o.dim;
            elements = new ArrayList<>(o.getElemsSize());
            for (double element : o.elements) 
            {
                elements.add(element);//adds at the end of the list
            }
        }

        VectorElem(String key, ArrayList<Double> elems) 
        {
            this.dim = elems.size();
            elements = new ArrayList<>(elems.size());
            for (double element : elems) 
            {
                elements.add(element);//adds at the end of the list
            }
        }

        @Override
        public void readFields(DataInput in) throws IOException 
        {
            elements = new ArrayList<>();
            this.dim = in.readInt();
            for (int i = 0; i < dim; i++) 
            {
                elements.add(in.readDouble());
            }
        }

        @Override
        public void write(DataOutput out) throws IOException 
        {
            out.writeInt(dim);
            for (double element : elements) 
            {
                out.writeDouble(element);
            }
        }//end write method

        @Override
        public int compareTo(VectorElem o) //standard compare uses all the attributes
        {
            int index = 0;
            for (double element : elements) //compare vectors
            {
                if (elements.get(index) != o.getElem(index)) 
                {
                    return (elements.get(index) < o.getElem(index)) ? -1 : 1;
                }
                index++;
            }
            return 0; // Should be for if all DIMs are equal
        }
        
        @Override
        public String toString() 
        {
            String elems = "";
            int index = 0;
            for (double element : elements) 
            {
                elems += elements.get(index);
                index++;
                if (index != elements.size()) 
                {
                    elems += ",";
                }
            }
            return (elems);
        }

        public double getDistanceBetween(VectorElem o) 
        {
            double dist = 0.0;

            int index = 0;
            for (double element : elements) 
            {
                dist += Math.pow((elements.get(index) - o.getElem(index)), 2);
                index++;
            }
            return Math.sqrt(dist);
        }

        public double getElem(int index) 
        {
            return elements.get(index);
        }

        public double setElem(int index, double elem) 
        {
            return elements.set(index, elem);
        }

        public int getElemsSize() 
        {
            return elements.size();
        }

    }//end VectorElem class
	
}//end GroupBy