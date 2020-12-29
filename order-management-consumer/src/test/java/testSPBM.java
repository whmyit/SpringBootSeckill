import com.dxhy.order.protocol.order.COMMON_ORDER_REQ;
import com.dxhy.order.protocol.order.ORDER_INVOICE_ITEM;
import com.dxhy.order.utils.JsonUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/4/8 13:38
 */
public class testSPBM {
    
    public static void main(String[] args) throws IOException {
        
        String[] a = {"1000000000000000000", "1010000000000000000", "1010100000000000000", "1010101000000000000", "1010102000000000000", "1010103000000000000", "1010104000000000000", "1010107000000000000", "1010108000000000000", "1010109000000000000", "1010111000000000000", "1010112000000000000", "1010113000000000000", "1010114000000000000", "1010114010000000000", "1010115000000000000", "1010115010000000000", "1010115030000000000", "1010116000000000000", "1010119000000000000", "1010200000000000000", "1010202000000000000", "1010203000000000000", "1010204000000000000", "1010300000000000000", "1010301000000000000", "1010302000000000000", "1010303000000000000", "1010303020000000000", "1010303030000000000", "1010304000000000000", "1010400000000000000", "1010500000000000000", "1020000000000000000", "1020100000000000000", "1020200000000000000", "1020201000000000000", "1020206000000000000", "1020300000000000000", "1020400000000000000", "1020401000000000000", "1020402000000000000", "1020403000000000000", "1020403030000000000", "1020404000000000000", "1020499000000000000", "1020500000000000000", "1020501000000000000", "1020502000000000000", "1020503000000000000", "1020504000000000000", "1020505000000000000", "1020506000000000000", "1020511000000000000", "1030000000000000000", "1030100000000000000", "1030101000000000000", "1030102000000000000", "1030102010000000000", "1030103000000000000", "1030104000000000000", "1030105000000000000", "1030105010000000000", "1030105020000000000", "1030106000000000000", "1030107000000000000", "1030107010000000000", "1030108000000000000", "1030109000000000000", "1030110000000000000", "1030111000000000000", "1030112000000000000", "1030112010000000000", "1030113000000000000", "1030114000000000000", "1030200000000000000", "1030201000000000000", "1030202000000000000", "1030202070000000000", "1030203000000000000", "1030204000000000000", "1030205000000000000", "1030206000000000000", "1030207000000000000", "1030208000000000000", "1030209000000000000", "1030210000000000000", "1030211000000000000", "1030212000000000000", "1030300000000000000", "1030301000000000000", "1030303000000000000", "1030307000000000000", "1030308000000000000", "1030309000000000000", "1030400000000000000", "1030401000000000000", "1030402000000000000", "1030403000000000000", "1040000000000000000", "1040100000000000000", "1040101000000000000", "1040102000000000000", "1040103000000000000", "1040104000000000000", "1040105000000000000", "1040106000000000000", "1040107000000000000", "1040108000000000000", "1040109000000000000", "1040110000000000000", "1040111000000000000", "1040112000000000000", "1040113000000000000", "1040114000000000000", "1040199000000000000", "1040200000000000000", "1040201000000000000", "1040202000000000000", "1040203000000000000", "1040204000000000000", "1040205000000000000", "1040300000000000000", "1040302000000000000", "1040303000000000000", "1040304000000000000", "1040305000000000000", "1040306000000000000", "1040307000000000000", "1050000000000000000", "1050100000000000000", "1050101000000000000", "1050102000000000000", "1050103000000000000", "1050104000000000000", "1050105000000000000", "1050105040000000000", "1050106000000000000", "1050107000000000000", "1050108000000000000", "1050109000000000000", "1050110000000000000", "1050200000000000000", "1050201000000000000", "1050202000000000000", "1060000000000000000", "1060100000000000000", "1060101000000000000", "1060102000000000000", "1060103000000000000", "1060104000000000000", "1060105000000000000", "1060200000000000000", "1060201000000000000", "1060201010000000000", "1060202000000000000", "1060204000000000000", "1060205000000000000", "1060300000000000000", "1060301000000000000", "1060301010000000000", "1060301020000000000", "1060301030000000000", "1060400000000000000", "1060401000000000000", "1060402000000000000", "1060402010000000000", "1060403000000000000", "1060404000000000000", "1060405000000000000", "1060406000000000000", "1060406020000000000", "1060407000000000000", "1060408000000000000", "1060409000000000000", "1060410000000000000", "1060500000000000000", "1060501000000000000", "1060502000000000000", "1060503000000000000", "1060504000000000000", "1060505000000000000", "1060506000000000000", "1060507000000000000", "1060508000000000000", "1060509000000000000", "1060509020000000000", "1060510000000000000", "1060510020000000000", "1060511000000000000", "1060512000000000000", "1060513000000000000", "1070000000000000000", "1070100000000000000", "1070101000000000000", "1070101010000000000", "1070101020000000000", "1070101030000000000", "1070101040000000000", "1070101050000000000", "1070101060000000000", "1070101070000000000", "1070101110000000000", "1070101130000000000", "1070102000000000000", "1070102020000000000", "1070102030000000000", "1070103000000000000", "1070200000000000000", "1070201000000000000", "1070201010000000000", "1070201020000000000", "1070202000000000000", "1070202010000000000", "1070202020000000000", "1070202020100000000", "1070203000000000000", "1070203010000000000", "1070203040000000000", "1070203040100000000", "1070203040200000000", "1070203040300000000", "1070203040400000000", "1070203040500000000", "1070203040600000000", "1070203040700000000", "1070203040800000000", "1070203049900000000", "1070204000000000000", "1070205000000000000", "1070206000000000000", "1070207000000000000", "1070208000000000000", "1070209000000000000", "1070210000000000000", "1070211000000000000", "1070212000000000000", "1070213000000000000", "1070213010000000000", "1070214000000000000", "1070215000000000000", "1070216000000000000", "1070217000000000000", "1070217060000000000", "1070218000000000000", "1070219000000000000", "1070220000000000000", "1070221000000000000", "1070222000000000000", "1070223000000000000", "1070224000000000000", "1070226000000000000", "1070299000000000000", "1070300000000000000", "1070301000000000000", "1070302000000000000", "1070303000000000000", "1070304000000000000", "1070305000000000000", "1070306000000000000", "1070307000000000000", "1070307070000000000", "1070308000000000000", "1070400000000000000", "1070401000000000000", "1070402000000000000", "1070403000000000000", "1070404000000000000", "1070500000000000000", "1070501000000000000", "1070504000000000000", "1070505000000000000", "1070506000000000000", "1070507000000000000", "1070508000000000000", "1070509000000000000", "1070510000000000000", "1070600000000000000", "1070601000000000000", "1070601010000000000", "1070602000000000000", "1070603000000000000", "1080000000000000000", "1080100000000000000", "1080101000000000000", "1080102000000000000", "1080103000000000000", "1080104000000000000", "1080104010000000000", "1080104030000000000", "1080105000000000000", "1080105010000000000", "1080105030000000000", "1080106000000000000", "1080107000000000000", "1080108000000000000", "1080109000000000000", "1080110000000000000", "1080110020000000000", "1080111000000000000", "1080112000000000000", "1080114000000000000", "1080115000000000000", "1080120000000000000", "1080121000000000000", "1080122000000000000", "1080124000000000000", "1080125000000000000", "1080127000000000000", "1080127010000000000", "1080128000000000000", "1080129000000000000", "1080130000000000000", "1080199000000000000", "1080200000000000000", "1080204000000000000", "1080207000000000000", "1080300000000000000", "1080302000000000000", "1080306000000000000", "1080310000000000000", "1080311000000000000", "1080311010000000000", "1080312000000000000", "1080312010000000000", "1080312020000000000", "1080312030000000000", "1080313000000000000", "1080313010000000000", "1080313020000000000", "1080314000000000000", "1080314010000000000", "1080314030000000000", "1080314030200000000", "1080314040000000000", "1080315000000000000", "1080316000000000000", "1080317000000000000", "1080318000000000000", "1080319000000000000", "1080320000000000000", "1080321000000000000", "1080322000000000000", "1080323000000000000", "1080324000000000000", "1080400000000000000", "1080401000000000000", "1080402000000000000", "1080403000000000000", "1080404000000000000", "1080405000000000000", "1080407000000000000", "1080411000000000000", "1080412000000000000", "1080413000000000000", "1080414000000000000", "1080415000000000000", "1080418000000000000", "1080419000000000000", "1090000000000000000", "1090100000000000000", "1090101000000000000", "1090102000000000000", "1090103000000000000", "1090104000000000000", "1090105000000000000", "1090106000000000000", "1090107000000000000", "1090108000000000000", "1090110000000000000", "1090111000000000000", "1090112000000000000", "1090113000000000000", "1090114000000000000", "1090115000000000000", "1090116000000000000", "1090117000000000000", "1090119000000000000", "1090120000000000000", "1090121000000000000", "1090122000000000000", "1090123000000000000", "1090124000000000000", "1090126000000000000", "1090127000000000000", "1090128000000000000", "1090129000000000000", "1090130000000000000", "1090131000000000000", "1090132000000000000", "1090133000000000000", "1090134000000000000", "1090135000000000000", "1090136000000000000", "1090137000000000000", "1090200000000000000", "1090201000000000000", "1090202000000000000", "1090203000000000000", "1090204000000000000", "1090205000000000000", "1090206000000000000", "1090207000000000000", "1090208000000000000", "1090209000000000000", "1090210000000000000", "1090211000000000000", "1090214000000000000", "1090215000000000000", "1090216000000000000", "1090217000000000000", "1090218000000000000", "1090219000000000000", "1090220000000000000", "1090221000000000000", "1090222000000000000", "1090223000000000000", "1090224000000000000", "1090225000000000000", "1090226000000000000", "1090227000000000000", "1090228000000000000", "1090229000000000000", "1090230000000000000", "1090231000000000000", "1090231010000000000", "1090232000000000000", "1090233000000000000", "1090235000000000000", "1090236000000000000", "1090236080000000000", "1090237000000000000", "1090238000000000000", "1090239000000000000", "1090240000000000000", "1090242000000000000", "1090243000000000000", "1090244000000000000", "1090245000000000000", "1090246000000000000", "1090247000000000000", "1090248000000000000", "1090249000000000000", "1090250000000000000", "1090251000000000000", "1090252000000000000", "1090253000000000000", "1090254000000000000", "1090255000000000000", "1090256000000000000", "1090300000000000000", "1090301000000000000", "1090305000000000000", "1090305010000000000", "1090305020000000000", "1090306000000000000", "1090307000000000000", "1090308000000000000", "1090310000000000000", "1090310010000000000", "1090310020000000000", "1090312000000000000", "1090314000000000000", "1090318000000000000", "1090321000000000000", "1090323000000000000", "1090324000000000000", "1090324010000000000", "1090325000000000000", "1090325010000000000", "1090326000000000000", "1090327000000000000", "1090328000000000000", "1090400000000000000", "1090401000000000000", "1090402000000000000", "1090403000000000000", "1090404000000000000", "1090405000000000000", "1090406000000000000", "1090407000000000000", "1090408000000000000", "1090409000000000000", "1090410000000000000", "1090411000000000000", "1090412000000000000", "1090413000000000000", "1090413010000000000", "1090413020000000000", "1090414000000000000", "1090415000000000000", "1090416000000000000", "1090417000000000000", "1090418000000000000", "1090419000000000000", "1090420000000000000", "1090421000000000000", "1090422000000000000", "1090423000000000000", "1090424000000000000", "1090425000000000000", "1090426000000000000", "1090500000000000000", "1090501000000000000", "1090502000000000000", "1090503000000000000", "1090504000000000000", "1090505000000000000", "1090506000000000000", "1090507000000000000", "1090508000000000000", "1090509000000000000", "1090510000000000000", "1090511000000000000", "1090512000000000000", "1090513000000000000", "1090514000000000000", "1090515000000000000", "1090516000000000000", "1090519000000000000", "1090522000000000000", "1090600000000000000", "1090601000000000000", "1090602000000000000", "1090603000000000000", "1090604000000000000", "1090605000000000000", "1090606000000000000", "1090607000000000000", "1090608000000000000", "1090609000000000000", "1090610000000000000", "1090611000000000000", "1090612000000000000", "1090613000000000000", "1090614000000000000", "1090615000000000000", "1090616000000000000", "1090617000000000000", "1090618000000000000", "1090619000000000000", "1090620000000000000", "1090621000000000000", "1090622000000000000", "1090623000000000000", "1090624000000000000", "1090625000000000000", "1090626000000000000", "1090626010000000000", "1090627000000000000", "1100000000000000000", "1100100000000000000", "1100101000000000000", "1100101010000000000", "1100101020000000000", "1100102000000000000", "1100102010000000000", "1100102020000000000", "1100200000000000000", "1100201000000000000", "1100202000000000000", "1100300000000000000", "1100301000000000000", "1100302000000000000", "2000000000000000000", "2010000000000000000", "3000000000000000000", "3010000000000000000", "3010100000000000000", "3010101000000000000", "3010101010000000000", "3010101020000000000", "3010101020100000000", "3010101020101000000", "3010101020200000000", "3010102000000000000", "3010102010000000000", "3010102020000000000", "3010102990000000000", "3010200000000000000", "3010201000000000000", "3010202000000000000", "3010203000000000000", "3010204000000000000", "3010300000000000000", "3010301000000000000", "3010301010000000000", "3010301020000000000", "3010301030000000000", "3010400000000000000", "3010401000000000000", "3010402000000000000", "3010403000000000000", "3010499000000000000", "3010500000000000000", "3010502000000000000", "3010502010000000000", "3010502020000000000", "3010503000000000000", "3010504000000000000", "3010506000000000000", "3010599000000000000", "3010600000000000000", "3019900000000000000", "3020000000000000000", "3020100000000000000", "3020101000000000000", "3020300000000000000", "3030000000000000000", "3030100000000000000", "3030200000000000000", "3040000000000000000", "3040100000000000000", "3040104000000000000", "3040104020000000000", "3040200000000000000", "3040201000000000000", "3040202000000000000", "3040300000000000000", "3040301000000000000", "3040303000000000000", "3040303010000000000", "3040304000000000000", "3040400000000000000", "3040401000000000000", "3040401010000000000", "3040401020000000000", "3040402000000000000", "3040403000000000000", "3040405000000000000", "3040407000000000000", "3040409000000000000", "3040500000000000000", "3040501000000000000", "3040502000000000000", "3040502010000000000", "3040502020000000000", "3040502020100000000", "3040502020300000000", "3040502029900000000", "3040600000000000000", "3040602000000000000", "3040603000000000000", "3040700000000000000", "3040701000000000000", "3040702000000000000", "3040703000000000000", "3040703030000000000", "3040800000000000000", "3040801000000000000", "3040802000000000000", "3040802010000000000", "3040802070000000000", "3040803000000000000", "3050000000000000000", "3060000000000000000", "3060100000000000000", "3060109000000000000", "3060110000000000000", "3060300000000000000", "3060301000000000000", "3060302000000000000", "3060400000000000000", "3070000000000000000", "3070100000000000000", "3070200000000000000", "3070201000000000000", "3070300000000000000", "3070400000000000000", "3070500000000000000", "4000000000000000000", "4010000000000000000", "4050000000000000000", "4050100000000000000", "5000000000000000000", "5010000000000000000", "5010100000000000000", "5010200000000000000", "6000000000000000000"};
        List<String> spbmList = Arrays.asList(a);
        System.out.println(JsonUtils.getInstance().toJsonString(spbmList));
        List<String> stringList = FileUtils.readLines(new File("C:\\Users\\ZSC-DXHY\\Downloads\\0417.txt"), StandardCharsets.UTF_8);
        
        for (int i = 0; i < stringList.size(); i++) {
            String s = stringList.get(i);
//            System.out.println(s);
            StringBuffer stringBuffer = new StringBuffer();
            
            COMMON_ORDER_REQ parseObject = JsonUtils.getInstance().parseObject(s, COMMON_ORDER_REQ.class);
            for (int j = 0; j < parseObject.getCOMMON_ORDERS().size(); j++) {
                
                List<ORDER_INVOICE_ITEM> order_invoice_items = parseObject.getCOMMON_ORDERS().get(j).getORDER_INVOICE_ITEMS();
                for (int m = 0; m < order_invoice_items.size(); m++) {
                    if (spbmList.contains(order_invoice_items.get(m).getSPBM())) {
                        stringBuffer.append("流水号:").append(parseObject.getCOMMON_ORDERS().get(j).getCOMMON_ORDER_HEAD().getDDQQLSH());
                        stringBuffer.append("第").append(m + 1).append("行,商品编码:").append(order_invoice_items.get(m).getSPBM()).append(",对应商品名称:").append(order_invoice_items.get(m).getXMMC()).append(",不合法").append("\r\n");
                        
                    }
                    
                }
            }
            
            System.out.println(stringBuffer.toString());
        }
    }
}