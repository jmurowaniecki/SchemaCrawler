/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.schemacrawler;


import java.util.Map;

public final class DatabaseConnectionOptions
  extends BaseDatabaseConnectionOptions
{

  private static final long serialVersionUID = -8141436553988174836L;

  public DatabaseConnectionOptions(final String connectionUrl)
    throws SchemaCrawlerException
  {
    super(new SingleUseUserCredentials(), toMap(connectionUrl));
  }

  public DatabaseConnectionOptions(final UserCredentials userCredentials,
                                   final Map<String, String> properties)
    throws SchemaCrawlerException
  {
    super(userCredentials, properties);
  }

}
