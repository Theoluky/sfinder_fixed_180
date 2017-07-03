package common.datastore;

import core.mino.Block;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class BlockCounterTest {
    @Test
    public void testEmpty() throws Exception {
        List<Block> emptyList = Collections.emptyList();
        BlockCounter counter = new BlockCounter(emptyList);
        assertThat(counter.getCounter(), is(0L));
    }

    @Test
    public void testAdd() throws Exception {
        BlockCounter counter = new BlockCounter(Arrays.asList(Block.I, Block.J));
        BlockCounter actual = counter.addAndReturnNew(Collections.singletonList(Block.T));

        assertThat(counter.getCounter(), is(new BlockCounter(Arrays.asList(Block.I, Block.J)).getCounter()));
        assertThat(actual.getCounter(), is(new BlockCounter(Arrays.asList(Block.I, Block.T, Block.J)).getCounter()));
    }

    @Test
    public void testGet() throws Exception {
        List<Block> blocks = Arrays.asList(Block.I, Block.J, Block.T, Block.S);
        BlockCounter counter = new BlockCounter(blocks);

        assertThat(counter.getBlockStream().collect(Collectors.toList()), is(counter.getBlocks()));
    }

    @Test
    public void testGetMap() throws Exception {
        List<Block> blocks = Arrays.asList(Block.I, Block.J, Block.T, Block.I, Block.I, Block.T, Block.S);
        BlockCounter counter = new BlockCounter(blocks);
        EnumMap<Block, Integer> map = counter.getEnumMap();
        assertThat(map.get(Block.I), is(3));
        assertThat(map.get(Block.T), is(2));
        assertThat(map.get(Block.S), is(1));
        assertThat(map.get(Block.J), is(1));
        assertThat(map.get(Block.L), is(nullValue()));
        assertThat(map.get(Block.Z), is(nullValue()));
        assertThat(map.get(Block.O), is(nullValue()));
    }
}