import java.nio.IntBuffer;

public class BufferTest {
    public static void main(String[] args){

        // wrap方法
//        int[] arr = new int[]{1, 2, 5};
//        IntBuffer buffer = IntBuffer.wrap(arr);
//        System.out.println(buffer);
//
//        IntBuffer buf2 = IntBuffer.wrap(arr, 0, 2);
//        System.out.println(buf2);

        // 其它方法
        IntBuffer buf1 = IntBuffer.allocate(10);
        int[] arr = new int[]{1,2,5};
        buf1.put(arr);
        System.out.println(buf1);

        // 一种复制方法
        IntBuffer buf3 = buf1.duplicate();
        System.out.println(buf3);

        // 设置buf1的位置属性
        buf1.position(1);
        System.out.println(buf1);
        System.out.println("可读数据为：" + buf1.remaining());

        int[] arr2 = new int[buf1.remaining()];
        // 将缓冲区数据放入arr2数组中
        buf1.get(arr2);
        for(int i : arr2){
            System.out.print(Integer.toString(i) + ", ");
        }
    }

}
