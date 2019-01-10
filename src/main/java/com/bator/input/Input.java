package com.bator.input;

import java.util.List;

public interface Input {

    List<InputChunk> gather(int retryCount);
}
