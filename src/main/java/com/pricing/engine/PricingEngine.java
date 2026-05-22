package com.pricing.engine;

import com.pricing.model.Cart;
import com.pricing.model.Customer;
import com.pricing.model.PricingContext;
import com.pricing.model.PricingResult;
import com.pricing.repository.RuleRepository;
import com.pricing.rule.PromotionRule;

import java.util.Comparator;
import java.util.List;

// Orchestrates rule loading, chain construction, and pricing execution
public final class PricingEngine {

    private final RuleRepository ruleRepository;

    public PricingEngine(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public PricingResult price(Cart cart, Customer customer) {
        List<PromotionRule> rules = ruleRepository.loadAll().stream()
                .sorted(Comparator.comparingInt(PromotionRule::getPriority))
                .toList();

        PromotionRule head = buildChain(rules);

        PricingContext ctx = new PricingContext(cart, customer);

        if (head != null) {
            head.apply(ctx);
        }

        return new PricingResult(cart.subtotal(), ctx.getRunningTotal(), ctx.getAppliedDiscounts());
    }

    private PromotionRule buildChain(List<PromotionRule> rules) {
        if (rules.isEmpty()) return null;
        PromotionRule head = rules.get(0);
        PromotionRule current = head;
        for (int i = 1; i < rules.size(); i++) {
            current.setNext(rules.get(i));
            current = rules.get(i);
        }
        return head;
    }
}
