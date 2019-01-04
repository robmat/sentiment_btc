package com.bator;

import java.net.MalformedURLException;
import java.util.List;

import com.bator.db.ChunkInserter;
import com.bator.input.InputChunk;
import com.bator.input.RedditInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AppTest {

    @InjectMocks
    App app = new App();

    @Mock
    RedditInput redditInput;

    @Mock
    ChunkInserter chunkInserter;

    @Test
    public void testStart()  {
        app.start(null);

        verify(redditInput).gather();
        verify(chunkInserter).insert(anyListOf(InputChunk.class));
    }
}
