package com.vitorcalliari.emprestimos.client.dto;

import java.util.List;

public record BcbODataResponse<T>(List<T> value) {}
