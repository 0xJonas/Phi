package de.delphi.phi.data;

import de.delphi.phi.PhiAccessException;
import de.delphi.phi.PhiException;
import de.delphi.phi.PhiStructureException;
import de.delphi.phi.PhiTypeException;
import org.junit.Test;

import static de.delphi.phi.Polyfill.assertThrows;
import static org.junit.Assert.*;

public class PhiCollectionTest {

    private PhiCollection a, b, c, d;

    private PhiCollection aSuper;
    private PhiCollection bSuper;

    private void setupInheritanceStructure() throws PhiException{
        /*
        //Setup:
        var a = [1, test1 = "a"]
        var b = [2, test1 = "b", test2 = "x"]
        var c = [3, 5, test1 = "c"]
        var d = [4, 6, test1 = "d", test2 = "y"]
        a.super = [b, c]
        b.super = [d]
        c.super = [d]
         */

        a = new PhiCollection();
        b = new PhiCollection();
        c = new PhiCollection();
        d = new PhiCollection();
        aSuper = new PhiCollection();
        bSuper = new PhiCollection();
        PhiCollection cSuper = new PhiCollection();

        //Setup inheritance
        aSuper.createMember(new PhiInt(1));
        aSuper.setUnnamed(0, b);
        aSuper.setUnnamed(1, c);
        a.setNamed("super", aSuper);

        bSuper.createMember(new PhiInt(0));
        bSuper.setUnnamed(0, d);
        b.setNamed("super", bSuper);

        cSuper.createMember(new PhiInt(0));
        cSuper.setUnnamed(0, d);
        c.setNamed("super", cSuper);

        //Fill collections
        a.createMember(new PhiSymbol("test1"));
        a.createMember(new PhiInt(0));
        a.setNamed("test1", new PhiString("a"));
        a.setUnnamed(0, new PhiInt(1));

        b.createMember(new PhiSymbol("test1"));
        b.createMember(new PhiSymbol("test2"));
        b.createMember(new PhiInt(0));
        b.setNamed("test1", new PhiString("b"));
        b.setNamed("test2", new PhiString("x"));
        b.setUnnamed(0, new PhiInt(2));

        c.createMember(new PhiSymbol("test1"));
        c.createMember(new PhiInt(1));
        c.setNamed("test1", new PhiString("c"));
        c.setUnnamed(0, new PhiInt(3));
        c.setUnnamed(1, new PhiInt(5));

        d.createMember(new PhiSymbol("test1"));
        d.createMember(new PhiSymbol("test2"));
        d.createMember(new PhiInt(1));
        d.setNamed("test1", new PhiString("d"));
        d.setNamed("test2", new PhiString("y"));
        d.setUnnamed(0, new PhiInt(4));
        d.setUnnamed(1, new PhiInt(6));
    }

    @Test
    public void testCreateMember() throws PhiException{
        PhiCollection mockCollection = new PhiCollection();

        assertThrows("Creating a member with a key of invalid type succeeded.", PhiTypeException.class,
                ()->mockCollection.createMember(new PhiFloat(1.0))
        );
        assertThrows("Creating a member with negative index succeeded.", PhiAccessException.class,
                ()->mockCollection.createMember(new PhiInt(-1))
        );

        mockCollection.createMember(new PhiInt(0));
        mockCollection.createMember(new PhiInt(99));
        mockCollection.createMember(new PhiSymbol("foo"));
        mockCollection.createMember(new PhiSymbol("bar"));

        assertEquals("'length' returned wrong value.",
                97 + 2 + 1,
                mockCollection.getNamed("length").longValue());
        assertEquals("New named members were not initialized to NULL.",
                PhiNull.NULL,
                mockCollection.getNamed("foo"));
        assertEquals("New unnamed members were not initialized to NULL.",
                PhiNull.NULL,
                mockCollection.getUnnamed(27));

        assertSame(mockCollection.getUnnamed(99), PhiNull.NULL);
        assertThrows("Access to member out of bounds succeeded.", PhiAccessException.class,
                ()->mockCollection.getUnnamed(100)
        );
    }

    @Test
    public void testUnnamed() throws PhiException{
        PhiCollection mockCollection = new PhiCollection();

        mockCollection.createMember(new PhiInt(1));
        mockCollection.setUnnamed(0, new PhiInt(16));
        mockCollection.setUnnamed(1,  new PhiString("test"));

        assertEquals("Could not retrieve unnamed member.",16, mockCollection.getUnnamed(0).longValue());
        assertEquals("Could not retrieve unnamed member.","test", mockCollection.getUnnamed(1).toString());

        assertThrows("Setting nonexistent unnamed member succeeded.", PhiAccessException.class,
                ()->mockCollection.setUnnamed(500, new PhiInt(1))
        );
        assertThrows("Retrieving nonexistent unnamed member succeeded.", PhiAccessException.class,
                ()->mockCollection.getUnnamed(500)
        );
        assertThrows("Setting member with negative index succeeded.", PhiAccessException.class,
                ()->mockCollection.setUnnamed(-1, new PhiInt(1))
        );
        assertThrows("Retrieving member with negative index succeeded.", PhiAccessException.class,
                ()->mockCollection.getUnnamed(-1)
        );

        setupInheritanceStructure();
        a.setUnnamed(1, new PhiInt(-1));
        assertEquals(-1, a.getUnnamed(1).longValue());
        assertEquals(-1, b.getUnnamed(1).longValue());
        assertEquals(5, c.getUnnamed(1).longValue());   //Should be unaffected
        assertEquals(-1, d.getUnnamed(1).longValue());

        c.setUnnamed(1, new PhiInt(38));
        assertEquals(-1, a.getUnnamed(1).longValue());
        assertEquals(-1, b.getUnnamed(1).longValue());
        assertEquals(38, c.getUnnamed(1).longValue());  //Should be the only one that changed
        assertEquals(-1, d.getUnnamed(1).longValue());

        assertThrows("Adding non-collection to superclass list succeeded.", PhiStructureException.class,
                ()->aSuper.setUnnamed(0, new PhiInt(100))
        );

        assertThrows("Creating circular inheritance succeeded.", PhiStructureException.class,
                ()->bSuper.setUnnamed(0, a)
        );
        assertSame("State got not rolled back correctly.", d, bSuper.getUnnamed(0));
    }

    @Test
    public void testNamed() throws PhiException{
        PhiCollection mockCollection = new PhiCollection();

        mockCollection.createMember(new PhiSymbol("foo"));
        mockCollection.createMember(new PhiSymbol("bar"));

        mockCollection.setNamed("foo", new PhiInt(16));
        mockCollection.setNamed("bar", new PhiString("test"));

        assertEquals("Could not retrieve named member.", 16, mockCollection.getNamed("foo").longValue());
        assertEquals("Could not retrieve named member.", "test", mockCollection.getNamed("bar").toString());

        assertThrows("Setting nonexistent named member succeeded.", PhiAccessException.class,
                ()->mockCollection.setNamed("fail", new PhiInt(1))
        );
        assertThrows("Retrieving nonexistent named member succeeded.", PhiAccessException.class,
                ()->mockCollection.getNamed("fail")
        );

        setupInheritanceStructure();
        a.setNamed("test2", new PhiString("phi"));
        assertEquals("phi", a.getNamed("test2").toString());
        assertEquals("phi", b.getNamed("test2").toString());
        assertEquals("y", c.getNamed("test2").toString());
        assertEquals("y", d.getNamed("test2").toString());

        assertThrows("Creating circular inheritance succeeded.", PhiStructureException.class,
                ()->b.setNamed("super", aSuper)
        );
        assertSame("State got not rolled back correctly.", bSuper, b.getNamed("super"));
    }

    @Test
    public void testSpecialMembers() throws PhiException{
        PhiCollection mockCollection = new PhiCollection();
        assertEquals("length is not 0 after instantiation.", 0, mockCollection.getNamed("length").longValue());
        assertEquals("'this' is not actually this.", mockCollection, mockCollection.getNamed("this"));

        assertThrows("Assignment to read-only member 'length' succeeded.", PhiAccessException.class,
                ()->mockCollection.setNamed("length", new PhiInt(1))
        );
        assertThrows("Assignment to read-only member 'this' succeeded.", PhiAccessException.class,
                ()->mockCollection.setNamed("this", new PhiInt(1))
        );

        setupInheritanceStructure();
        assertEquals(2, a.getNamed("length").longValue());
        assertEquals(2, b.getNamed("length").longValue());
        assertEquals(2, c.getNamed("length").longValue());
        assertEquals(2, d.getNamed("length").longValue());
    }

    @Test
    public void testInheritance() throws PhiException{
        setupInheritanceStructure();

        //check integrity
        assertEquals("Collection a not properly filled.", "a", a.getNamed("test1").toString());
        assertEquals("Collection b not properly filled.", "b", b.getNamed("test1").toString());
        assertEquals("Collection c not properly filled.", "c", c.getNamed("test1").toString());
        assertEquals("Collection d not properly filled.", "d", d.getNamed("test1").toString());

        //test basic inheritance
        assertEquals("Inheritance not working.", "x", a.getNamed("test2").toString());
        assertEquals("Inheritance not working.", "y", c.getNamed("test2").toString());

        a.setNamed("test2", new PhiString("z"));

        //Test modifying superclass members
        assertEquals("Superclass members not correctly modified.", "z", a.getNamed("test2").toString());
        assertEquals("Superclass members not correctly modified.", "z", b.getNamed("test2").toString());
        assertEquals("Superclass members not correctly modified.", "y", c.getNamed("test2").toString());
        assertEquals("Superclass members not correctly modified.", "y", d.getNamed("test2").toString());

        a.setNamed("super", new PhiCollection());
        aSuper.setUnnamed(1, a);    //Should NOT throw an exception since a no longer has super classes
    }

    @Test
    public void testClone() throws CloneNotSupportedException, PhiException{
        setupInheritanceStructure();

        PhiCollection copy = (PhiCollection) d.clone();
        assertNotSame(copy, d);
        assertEquals(d.getUnnamed(0).longValue(), copy.getUnnamed(0).longValue());
        assertNotSame(d.getUnnamed(1), copy.getUnnamed(1));
        assertEquals(d.getNamed("test1").toString(), copy.getNamed("test1").toString());
        assertNotSame(d.getNamed("test2"), copy.getNamed("test2"));

        PhiCollection copy2 = (PhiCollection) a.clone();
        assertEquals(copy2.getUnnamed(1).longValue(), d.getUnnamed(1).longValue());
        assertNotSame(copy2.getUnnamed(1), d.getUnnamed(1));
        assertEquals(copy2.getSuperClasses().getUnnamed(0).getUnnamed(0).longValue(),
                b.getUnnamed(0).longValue());
        assertNotSame(copy2.getSuperClasses().getUnnamed(0), b);
    }
}