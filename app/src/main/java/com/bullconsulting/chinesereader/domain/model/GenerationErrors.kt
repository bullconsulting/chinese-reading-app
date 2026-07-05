package com.bullconsulting.chinesereader.domain.model

/** Thrown when a generation is attempted but no Qwen API key has been set. */
class MissingApiKeyException : Exception("No Qwen API key set. Add it in Settings.")
