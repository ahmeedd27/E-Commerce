package com.ahmed.E_CommerceApp.dto;

import lombok.NoArgsConstructor;

/**
 * Marker class — all fields live in ProductRequest.
 * Kept as a separate type so controller signatures and
 * Swagger docs remain explicit about intent.
 */
@NoArgsConstructor
public class ProductUpdateRequest extends ProductRequest {
}