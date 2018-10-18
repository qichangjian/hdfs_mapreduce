package com.qcj.map_reduce.mapreduce_sort_writable_test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 在三题目的结果上 再按照  平均分  进行降序排序
 */
public class StudentAvgDesc {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        try {
            Job job = Job.getInstance(configuration);
            job.setJarByClass(StudentAvgDesc.class);
            job.setMapperClass(MyMapper.class);
            job.setReducerClass(MyReducer.class);

            job.setMapOutputKeyClass(IntWritable.class);
            job.setMapOutputValueClass(Text.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);

            System.setProperty("HASOOP_USER_NAME","hadoop1");
            Path inpath = new Path("hdfs://hadoop1:9000/out_avg_score3");
            FileInputFormat.addInputPath(job,inpath);
            Path outpath = new Path("hdfs://hadoop1:9000/out_avg_score4");
            FileOutputFormat.setOutputPath(job,outpath);

            job.waitForCompletion(true);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static class MyMapper extends Mapper<LongWritable, Text, IntWritable,Text> {
        IntWritable mk = new IntWritable();
        Text mv = new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] datas = value.toString().split("\t");
            mk.set(-Integer.parseInt(datas[datas.length-1]));
            StringBuilder sbmv = new StringBuilder();
            for (int i = 0; i < datas.length-1; i++) {
                if(i == datas.length - 2){
                    sbmv.append(datas[i]);
                }else{
                    sbmv.append(datas[i]).append("\t");
                }
            }
            mv.set(sbmv.toString());
            context.write(mk,mv);
        }
    }
    static class MyReducer extends Reducer<IntWritable,Text,Text,IntWritable> {
        @Override
        protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text v:values) {
                context.write(v,new IntWritable(-key.get()));
            }
        }
    }
}
