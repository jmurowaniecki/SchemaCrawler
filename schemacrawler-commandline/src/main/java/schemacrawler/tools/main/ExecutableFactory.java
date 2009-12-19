/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.main;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputOptions;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class ExecutableFactory
{

  private static final Logger LOGGER = Logger.getLogger(ExecutableFactory.class
    .getName());

  /**
   * Parses the command line.
   * 
   * @param args
   *        Command line arguments
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  static List<Executable<?>> createExecutables(final SchemaCrawlerCommandLine commandLine)
    throws SchemaCrawlerException
  {
    if (commandLine == null)
    {
      throw new SchemaCrawlerException("No command line provided");
    }

    final Map<String, Class<? extends Executable>> commandRegistry = loadCommandRegistry();

    final Config config = commandLine.getConfig();
    final SchemaCrawlerOptions schemaCrawlerOptions = commandLine
      .getSchemaCrawlerOptions();
    final OutputOptions masterOutputOptions = commandLine.getOutputOptions();
    final Commands commands = commandLine.getCommands();
    final List<Executable<?>> executables = new ArrayList<Executable<?>>();
    for (final String command: commands)
    {
      final OutputOptions outputOptions = masterOutputOptions.duplicate();
      if (commands.isFirstCommand(command))
      {
        // First command - no footer
        outputOptions.setNoFooter(true);
      }
      else if (commands.isLastCommand(command))
      {
        // Last command - no header, or info
        outputOptions.setNoHeader(true);
        outputOptions.setNoInfo(true);

        outputOptions.setAppendOutput(true);
      }
      else
      {
        // Middle command - no header, footer, or info
        outputOptions.setNoHeader(true);
        outputOptions.setNoInfo(true);
        outputOptions.setNoFooter(true);

        outputOptions.setAppendOutput(true);
      }

      final Class<? extends Executable> executableClass;
      if (commandRegistry.containsKey(command))
      {
        executableClass = commandRegistry.get(command);
      }
      else
      {
        executableClass = commandRegistry.get("default");
      }

      final Executable<?> executable;
      try
      {
        executable = executableClass.newInstance();
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.initializeToolOptions(command, config, outputOptions);
      }
      catch (final InstantiationException e)
      {
        throw new SchemaCrawlerException("Could not initialize executable for "
                                         + command, e);
      }
      catch (final IllegalAccessException e)
      {
        throw new SchemaCrawlerException("Could not initialize executable for "
                                         + command, e);
      }
      executables.add(executable);
    }

    return executables;
  }

  private static Map<String, Class<? extends Executable>> loadCommandRegistry()
    throws SchemaCrawlerException
  {
    // Load commands registry
    final Map<String, Class<? extends Executable>> commandRegistry = new HashMap<String, Class<? extends Executable>>();
    final Enumeration<URL> resources;
    try
    {
      resources = Thread.currentThread().getContextClassLoader()
        .getResources("command.properties");
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not load command registry");
    }
    while (resources.hasMoreElements())
    {
      final URL commandRegistryUrl = resources.nextElement();
      try
      {
        final Properties commandRegistryProperties = new Properties();
        commandRegistryProperties.load(commandRegistryUrl.openStream());
        final Enumeration<?> propertyNames = commandRegistryProperties
          .propertyNames();
        while (propertyNames.hasMoreElements())
        {
          final String commandName = (String) propertyNames.nextElement();
          final String executableClassName = commandRegistryProperties
            .getProperty(commandName);
          try
          {
            final Class<? extends Executable> executableClass = (Class<? extends Executable>) Class
              .forName(executableClassName);
            commandRegistry.put(commandName, executableClass);
          }
          catch (final ClassNotFoundException e)
          {
            LOGGER.log(Level.WARNING,
                       "Could not load command, " + commandName,
                       e);
          }
        }
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Could not load command registry, "
                                  + commandRegistryUrl, e);
      }
    }
    if (commandRegistry.isEmpty())
    {
      throw new SchemaCrawlerException("Could not load command registry");
    }
    return commandRegistry;
  }

  private ExecutableFactory()
  {

  }

}
