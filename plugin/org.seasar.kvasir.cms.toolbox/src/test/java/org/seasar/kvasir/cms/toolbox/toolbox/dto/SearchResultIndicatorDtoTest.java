package org.seasar.kvasir.cms.toolbox.toolbox.dto;

import org.seasar.kvasir.cms.toolbox.toolbox.dto.SearchResultIndicatorDto.Element;

import junit.framework.TestCase;


public class SearchResultIndicatorDtoTest extends TestCase
{
    public void test()
        throws Exception
    {
        SearchResultIndicatorDto target = new SearchResultIndicatorDto(38, 10,
            19, 5);
        Element[] actual = target.getElements();

        assertEquals(4, actual.length);
        int idx = 0;
        assertFalse(actual[idx++].isCurrent());
        assertTrue(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());

        target = new SearchResultIndicatorDto(68, 10, 29, 5);
        actual = target.getElements();

        assertEquals(7, actual.length);
        idx = 0;
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertTrue(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());

        target = new SearchResultIndicatorDto(108, 10, 49, 5);
        actual = target.getElements();

        assertEquals(11, actual.length);
        idx = 0;
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertTrue(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());

        target = new SearchResultIndicatorDto(208, 10, 109, 5);
        actual = target.getElements();

        assertEquals(21, actual.length);
        idx = 0;
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertNull(actual[idx++]);
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertTrue(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertNull(actual[idx++]);
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
    }


    public void test_displayPageRangeがALLで全てがレンダリング対象になること()
        throws Exception
    {
        SearchResultIndicatorDto target = new SearchResultIndicatorDto(208, 10,
            109, SearchResultIndicatorDto.DISPLAYPAGERANGE_ALL);
        Element[] actual = target.getElements();

        assertEquals(21, actual.length);
        int idx = 0;
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertTrue(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
        assertFalse(actual[idx++].isCurrent());
    }
}
