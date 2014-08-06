import uuid
import json
import os

######### Default Values ###########

# Define default widget size
DEF_HEIGHT = 700
DEF_WIDTH = 1160

# Default network
DEF_NODES = [
    { 'data': { 'id': 'Network Data' } },
    { 'data': { 'id': 'Empty' } }
]

DEF_EDGES = [
    { 'data': { 'id': 'is', 'source': 'Network Data', 'target': 'Empty' } }
]

DEF_LAYOUT = 'preset'
DEF_STYLE = 'default'

PRESET_LAYOUTS = {
    'Preset': 'preset',
    'Circle':'circle',
    'Concentric':'concentric',
    'Breadthfirst':'breadthfirst',
    'Spring':'cose',
    'Grid':'grid'
}

DEF_SCALING = 1.0

HTML_TEMPLATE_FILE = 'template.html'


def render(network, style=DEF_STYLE, layout_algorithm=DEF_LAYOUT, height=DEF_HEIGHT, width=DEF_WIDTH):
    from jinja2 import Template
    from IPython.core.display import display, HTML

    if network==None:
        nodes = DEF_NODES
        edges = DEF_EDGES
    else:
        nodes = network['elements']['nodes']
        edges = network['elements']['edges']

    path = os.path.abspath(os.path.dirname(__file__)) + '/' + HTML_TEMPLATE_FILE
    template = Template(open(path).read())
    cyjs_widget = template.render(nodes = json.dumps(nodes), edges = json.dumps(edges), 
        uuid="cy" + str(uuid.uuid4()), widget_width = str(width), widget_height = str(height), 
        layout=layout_algorithm, style_json=json.dumps(style))

    return display(HTML(cyjs_widget))

# List of available layout algorithms
def get_layouts():
    return PRESET_LAYOUTS


# Convert to Cytoscape.js format from NetworkX object
def from_networkx(networkx_graph):
    new_graph = {}
    elements = {}
    nodes = []
    edges = []

    nodes_x = networkx_graph.nodes();
    edges_x = networkx_graph.edges();

    # Network Attributes
    net_attr_keys = networkx_graph.graph.keys();
    network_data = {}
    for net_key in net_attr_keys:
        network_data[net_key] = networkx_graph.graph[net_key]
    
    new_graph['data'] = network_data

    for node in nodes_x:
        new_node = {}
        data = {}
        data['id'] = str(node)
        data['name'] = str(node)
        for key in networkx_graph.node[node].keys():
            data[key] = networkx_graph.node[node][key]
        new_node['data'] = data
        nodes.append(new_node)

    for edge in edges_x:
        new_edge = {}
        data = {}
        data['source'] = str(edge[0])
        data['target'] = str(edge[1])
        
        new_edge['data'] = data
        edges.append(new_edge)

    elements['nodes'] = nodes
    elements['edges'] = edges
    new_graph['elements'] = elements

    return new_graph


def from_igraph(igraph_network, layout, scale=DEF_SCALING):
    new_graph = {}
    elements = {}
    nodes = []
    edges = []

    el = igraph_network.get_edgelist()
    nodes_original = igraph_network.vs;

    node_attr = igraph_network.vs.attributes()

    idx = 0
    for node in nodes_original:
        new_node = {}
        data = {}
        data['id'] = str(node.index)
        data['name'] = str(node.index)
        for key in node_attr:
            data[key] = node[key]
        new_node['data'] = data
        if layout is not None:
            position = {}
            position['x'] = layout[idx][0] * scale
            position['y'] = layout[idx][1] * scale
            new_node['position'] = position

        nodes.append(new_node)
        idx = idx + 1

    for edge in el:
        new_edge = {}
        data = {}
        data['source'] = str(edge[0])
        data['target'] = str(edge[1])
        new_edge['data'] = data
        edges.append(new_edge)

    elements['nodes'] = nodes
    elements['edges'] = edges
    new_graph['elements'] = elements

    return new_graph

def from_sgraph(sgraph):
    new_graph = {}
    elements = {}
    nodes = []
    edges = []

    nodes_original = sgraph.vertices
    el = sgraph.edges;

    node_attr = nodes_original[0].keys()

    for node in nodes_original:
        new_node = {}
        data = {}
        data['id'] = node['__id']
        data['name'] = node['__id']
        for key in node_attr:
            data[key] = node[key]
        new_node['data'] = data
        nodes.append(new_node)


    for edge in el:
        new_edge = {}
        data = {}
        data['source'] = str(edge['__src_id'])
        data['target'] = str(edge['__dst_id'])
        new_edge['data'] = data
        edges.append(new_edge)

    elements['nodes'] = nodes
    elements['edges'] = edges
    new_graph['elements'] = elements

    return new_graph


def embedShare(url, width=DEF_WIDTH, height=DEF_HEIGHT):
    from IPython.core.display import display
    from IPython.lib.display import IFrame
    return display(IFrame(url, width, height))