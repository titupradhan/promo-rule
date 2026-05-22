package com.pricing.repository;

import com.pricing.rule.*;

import java.util.List;

/**
 * Pattern: Factory (light) — constructs the 6 canonical promotion rules.
 * In production, this would be backed by a DB table or JSON config loaded at startup.
 */
public final class InMemoryRuleRepository implements RuleRepository {

    @Override
    public List<PromotionRule> loadAll() {
        return List.of(
                new BulkQuantityRule(),
                new HighValueItemRule(),
                new ElectronicsCategoryRule(),
                new CartValueRule(),
                new PremiumUserRule(),
                new CouponRule()
        );
    }
}
