-- 주문 테이블 생성
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 주문 항목 테이블 생성
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price INT NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 인덱스 생성: 사용자 기반 주문 목록 조회 최적화
CREATE INDEX idx_orders_user_id ON orders(user_id);

-- 인덱스 생성: 주문 상태 + 주문 시점 기준 조회 (선택적)
-- CREATE INDEX idx_orders_status_ordered_at ON orders(status, ordered_at);

-- 인덱스 생성: 주문 ID 기준으로 상품 항목 조회
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- 인덱스 생성: 특정 상품 기준 주문 이력 분석
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
