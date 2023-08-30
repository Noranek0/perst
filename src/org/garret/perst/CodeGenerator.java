package org.garret.perst;

public interface CodeGenerator {
    public interface Code {
    }

    void predicate(Code code);
    Code literal(Object value);
    Code list(Code... values);
    Code parameter(int n, Class type);
    Code field(String name);
    Code field(Code base, String name);
    Code invoke(Code base, String name, Code... arguments);
    Code invoke(String name, Code[] arguments);
    Code and(Code left, Code right);
    Code or(Code left, Code right);
    Code add(Code left, Code right);
    Code sub(Code left, Code right);
    Code mul(Code left, Code right);
    Code div(Code left, Code right);
    Code pow(Code left, Code right);
    Code eq(Code left, Code right);
    Code ge(Code left, Code right);
    Code gt(Code left, Code right);
    Code lt(Code left, Code right);
    Code le(Code left, Code right);
    Code ne(Code left, Code right);
    Code neg(Code expr);
    Code abs(Code expr);
    Code not(Code expr);
    Code between(Code expr, Code low, Code high);
    Code like(Code expr, Code pattern, Code esc);
    Code like(Code expr, Code pattern);
    Code in(Code expr, Code set);
    Code sin(Code expr);
    Code cos(Code expr);
    Code tan(Code expr);
    Code asin(Code expr);
    Code acos(Code expr);
    Code atan(Code expr);
    Code sqrt(Code expr);
    Code exp(Code expr);
    Code log(Code expr);
    Code ceil(Code expr);
    Code floor(Code expr);
    Code lower(Code expr);
    Code upper(Code expr);
    Code length(Code expr);
    Code getAt(Code array, Code index);
    Code integer(Code expr);
    Code real(Code expr);
    Code string(Code expr);

    void orderBy(String name, boolean ascent);
    void orderBy(String name);
}